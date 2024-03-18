package me.supcheg.advancedmanhunt.game.impl;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.action.Action;
import me.supcheg.advancedmanhunt.action.ActionExecutor;
import me.supcheg.advancedmanhunt.action.DefaultActionExecutor;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.event.ManHuntGameStartEvent;
import me.supcheg.advancedmanhunt.event.ManHuntGameStopEvent;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.gui.ConfigurateGameGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.Players;
import me.supcheg.advancedmanhunt.random.ThreadSafeRandom;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.text.MessageText;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static me.supcheg.advancedmanhunt.action.Action.anyThread;
import static me.supcheg.advancedmanhunt.action.Action.join;
import static me.supcheg.advancedmanhunt.action.Action.mainThread;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@CustomLog
class DefaultManHuntGameService implements Listener {
    private final ManHuntGameRepository gameRepository;
    private final GameRegionRepository gameRegionRepository;
    private final TemplateRepository templateRepository;
    private final TemplateLoader templateLoader;
    private final PlayerReturner playerReturner;
    private final PlayerFreezer playerFreezer;
    private final ActionExecutor actionExecutor;
    private final AdvancedGuiController guiController;

    DefaultManHuntGameService(@NotNull ManHuntGameRepository gameRepository,
                              @NotNull GameRegionRepository gameRegionRepository,
                              @NotNull TemplateRepository templateRepository,
                              @NotNull TemplateLoader templateLoader,
                              @NotNull PlayerReturner playerReturner,
                              @NotNull PlayerFreezer playerFreezer,
                              @NotNull AdvancedGuiController guiController) {
        this.gameRepository = gameRepository;
        this.gameRegionRepository = gameRegionRepository;
        this.templateRepository = templateRepository;
        this.templateLoader = templateLoader;
        this.playerReturner = playerReturner;
        this.playerFreezer = playerFreezer;
        this.actionExecutor = new DefaultActionExecutor(BukkitUtil.mainThreadExecutor(), Executors.newFixedThreadPool(2));
        this.guiController = guiController;
    }

    @NotNull
    CompletableFuture<Boolean> start(@NotNull DefaultManHuntGame game) {
        return new StartManHuntGameRunnable(game).execute();
    }

    @NotNull
    private Template findTemplate(@NotNull String key) {
        return Objects.requireNonNull(templateRepository.getEntity(key), "template");
    }

    @NotNull
    public ConfigurateGameGui createConfigGui(@NotNull DefaultManHuntGame game) {
        return new ConfigurateGameGui(guiController, game);
    }

    public void unregisterConfigGui(@NotNull ConfigurateGameGui gui) {
        guiController.unregister(gui.getCurrentKey());
    }

    @RequiredArgsConstructor
    private class StartManHuntGameRunnable {
        private final DefaultManHuntGame game;

        private Template overworldTemplate;
        private Template netherTemplate;
        private Template endTemplate;

        private ImmutableLocation runnerLocation;
        private List<ImmutableLocation> huntersLocations;
        private ImmutableLocation spectatorsLocation;

        private FreezeGroup freezeGroup;
        private CountDownTimer startTimer;

        @NotNull
        public CompletableFuture<Boolean> execute() {
            Action action = join(
                    anyThread("assert_can_start")
                            .execute(() -> {
                                if (!game.canStart()) {
                                    throw new IllegalStateException("Can't start the game without players");
                                }
                            }),
                    anyThread("set_load_state")
                            .execute(() -> game.setState(GameState.LOAD))
                            .discard(() -> game.setState(GameState.CREATE)),
                    mainThread("freeze_config")
                            .execute(() -> {
                                game.unregisterConfigGui();
                                game.getConfig().freeze();
                            }),
                    mainThread("load_regions")
                            .execute(() -> {
                                game.setOverworldRegion(gameRegionRepository.getAndReserveRegion(RealEnvironment.OVERWORLD));
                                game.setNetherRegion(gameRegionRepository.getAndReserveRegion(RealEnvironment.NETHER));
                                game.setEndRegion(gameRegionRepository.getAndReserveRegion(RealEnvironment.THE_END));
                            })
                            .discard(() -> {
                                setNotReservedIfNonNull(game.getOverworld());
                                game.setOverworldRegion(null);

                                setNotReservedIfNonNull(game.getNether());
                                game.setNetherRegion(null);

                                setNotReservedIfNonNull(game.getEnd());
                                game.setEndRegion(null);
                            }),
                    anyThread("find_templates")
                            .execute(() -> {
                                overworldTemplate = findTemplate(game.getConfig().getOverworldTemplate());
                                netherTemplate = findTemplate(game.getConfig().getNetherTemplate());
                                endTemplate = findTemplate(game.getConfig().getEndTemplate());
                            })
                            .discard(() -> {
                                overworldTemplate = null;
                                netherTemplate = null;
                                endTemplate = null;
                            }),
                    anyThread("unload_regions")
                            .execute(() -> {
                                boolean notUnloaded = !game.getOverworld().unload()
                                        || !game.getNether().unload()
                                        || !game.getEnd().unload();
                                if (notUnloaded) {
                                    throw new IllegalStateException("Can't unload regions for " + game);
                                }
                            }),
                    anyThread("load_templates")
                            .execute(() -> {
                                templateLoader.loadTemplate(game.getOverworld(), overworldTemplate);
                                templateLoader.loadTemplate(game.getNether(), netherTemplate);
                                templateLoader.loadTemplate(game.getEnd(), endTemplate);
                            }),
                    anyThread("set_start_state")
                            .execute(() -> game.setState(GameState.START))
                            .discard(() -> game.setState(GameState.LOAD)),
                    anyThread("randomize_roles_if_enabled")
                            .execute(() -> {
                                if (!game.getConfig().isRandomizeRolesOnStart()) {
                                    return;
                                }

                                List<UUID> players = new ArrayList<>(game.getPlayers());

                                UUID newRunner = ThreadSafeRandom.randomElement(players);
                                players.remove(newRunner);

                                game.getAllMembers().removeAll(ManHuntRole.RUNNER);
                                game.getAllMembers().put(ManHuntRole.RUNNER, newRunner);

                                game.getAllMembers().replaceValues(ManHuntRole.HUNTER, players);
                            }),
                    anyThread("find_spawn_locations")
                            .execute(() -> {
                                List<SpawnLocationFindResult> spawnLocations = overworldTemplate.getSpawnLocations();
                                SpawnLocationFinder spawnLocationFinder = CachedSpawnLocationFinder.randomFrom(spawnLocations);
                                SpawnLocationFindResult locations =
                                        spawnLocationFinder.find(game.getOverworld(), game.getHunters().size());

                                runnerLocation = locations.getRunnerLocation();
                                huntersLocations = locations.getHuntersLocations();
                                spectatorsLocation = locations.getSpectatorsLocation();

                                game.setSpawnLocation(runnerLocation);
                            })
                            .discard(() -> {
                                runnerLocation = null;
                                huntersLocations = null;
                                spectatorsLocation = null;
                            }),
                    mainThread("setup_region_portal_handler")
                            .execute(() -> {
                                RegionPortalHandler portalHandler = new RegionPortalHandler(
                                        gameRegionRepository,
                                        game.getOverworld(), game.getNether(), game.getEnd(),
                                        runnerLocation
                                );
                                BukkitUtil.registerEventListener(portalHandler);
                                game.setPortalHandler(portalHandler);
                            })
                            .discard(() -> {
                                if (game.getPortalHandler() != null) {
                                    game.getPortalHandler().close();
                                    game.setPortalHandler(null);
                                }
                            }),
                    anyThread("freeze_players")
                            .execute(() -> {
                                freezeGroup = playerFreezer.newFreezeGroup();
                                game.getMembers().forEach(freezeGroup::add);
                            })
                            .discard(() -> {
                                if (freezeGroup != null) {
                                    freezeGroup.clear();
                                    freezeGroup = null;
                                }
                            }),
                    mainThread("teleport_players")
                            .execute(() -> {
                                Player runner = Players.getPlayer(game.getRunner());

                                runner.teleport(runnerLocation.asMutable());
                                runner.getInventory().clear();
                                runner.setGameMode(GameMode.ADVENTURE);

                                ItemStack compass = new ItemStack(Material.COMPASS);

                                int i = 0;
                                for (Player hunter : Players.asPlayersView(game.getHunters())) {
                                    hunter.teleport(huntersLocations.get(i).asMutable());
                                    hunter.setGameMode(GameMode.ADVENTURE);
                                    hunter.getInventory().clear();
                                    hunter.getInventory().setItem(0, compass);
                                    i++;
                                }

                                Location spectatorsLocationMutable = spectatorsLocation.asMutable();
                                Players.forEach(game.getSpectators(),
                                        spectator -> {
                                            spectator.teleport(spectatorsLocationMutable);
                                            spectator.setGameMode(GameMode.SPECTATOR);
                                        }
                                );
                            })
                            .discard(() -> {
                                Players.forEach(game.getSpectators(), playerReturner::returnPlayer);
                                Players.forEach(game.getHunters(), playerReturner::returnPlayer);

                                Player runner = Bukkit.getPlayer(game.getRunner());
                                if (runner != null) {
                                    playerReturner.returnPlayer(runner);
                                }
                            }),
                    anyThread("schedule_start_timer")
                            .execute(() -> {
                                        startTimer = CountDownTimer.builder()
                                                .everyPeriod(left -> MessageText.START_IN.sendUniqueIds(game.getMembers(), left))
                                                .afterComplete(() -> {
                                                    MessageText.START.sendUniqueIds(game.getMembers());
                                                    Players.forEach(game.getPlayers(),
                                                            player -> player.setGameMode(GameMode.SURVIVAL)
                                                    );

                                                    freezeGroup.clear();
                                                    game.setState(GameState.PLAY);
                                                    game.setStartTime(System.currentTimeMillis());
                                                    new ManHuntGameStartEvent(game).callEvent();
                                                })
                                                .times(15)
                                                .schedule();
                                        game.getTimers().add(startTimer);
                                    }
                            )
                            .discard(() -> {
                                if (startTimer != null) {
                                    startTimer.cancel();
                                    game.getTimers().remove(startTimer);
                                    startTimer = null;
                                }
                            })
            );
            return actionExecutor.execute(action)
                    .thenApply(throwables -> {
                        for (Throwable thr : throwables) {
                            log.error("An error occurred while starting {}", game, thr);
                        }
                        return throwables.isEmpty();
                    });
        }

        private void setNotReservedIfNonNull(@Nullable GameRegion region) {
            if (region != null) {
                region.setReserved(false);
            }
        }
    }

    void stop(@NotNull DefaultManHuntGame game, @Nullable ManHuntRole winnerRole) {
        log.debugIfEnabled("Stopping game {}. Winner: {}", game.getUniqueId(), winnerRole);

        if (game.getState().ordinal() >= GameState.STOP.ordinal()) {
            throw new IllegalStateException("The game has already been stopped or is in the process of clearing");
        }

        if (winnerRole == ManHuntRole.SPECTATOR) {
            throw new IllegalArgumentException("Available parameters are %s, %s or null"
                    .formatted(ManHuntRole.RUNNER, ManHuntRole.HUNTER));
        }

        game.setState(GameState.STOP);
        new ManHuntGameStopEvent(game).callEvent();

        clear(game);
    }

    void clear(@NotNull DefaultManHuntGame game) {
        if (game.getState().ordinal() >= GameState.CLEAR.ordinal()) {
            throw new IllegalStateException("The game is already in the process of being cleaned up");
        }
        game.setState(GameState.CLEAR);

        game.getPortalHandler().close();

        for (CountDownTimer timer : game.getTimers()) {
            timer.cancel();
        }
        for (FreezeGroup group : game.getFreezeGroups()) {
            group.clear();
        }

        Players.forEach(game.getMembers(), playerReturner::returnPlayer);

        game.getOverworld().setReserved(false);
        game.getNether().setReserved(false);
        game.getEnd().setReserved(false);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleHunterInteract(@NotNull PlayerInteractEvent event) {
        Player hunter = event.getPlayer();

        DefaultManHuntGame game = getGame(event.getPlayer().getLocation());
        if (isNullOrCreateState(game) || game.getRole(hunter.getUniqueId()) != ManHuntRole.HUNTER) {
            return;
        }

        ItemStack itemStack = event.getItem();
        if (itemStack != null && itemStack.getType() == Material.COMPASS) {
            event.setCancelled(true);

            UUID runnerUniqueId = game.getRunner();
            Objects.requireNonNull(runnerUniqueId);

            Player runner = Bukkit.getPlayer(runnerUniqueId);
            String runnerName;

            Location runnerLocation;
            if (runner != null) {
                runnerLocation = runner.getLocation();
                runnerName = runner.getName();
            } else {
                runnerLocation = ImmutableLocation.mutableCopy(
                        game.getEnvironmentToRunnerLastLocation()
                                .get(RealEnvironment.fromWorld(hunter.getWorld()))
                );
                runnerName = Objects.requireNonNull(Bukkit.getOfflinePlayer(runnerUniqueId).getName(), "runnerName");
            }

            if (runnerLocation == null) {
                log.error("runnerLocation is null. Last locations: {}", game.getEnvironmentToRunnerLastLocation());
                return;
            }

            CompassMeta meta = (CompassMeta) itemStack.getItemMeta();
            meta.setLodestoneTracked(false);
            meta.setLodestone(runnerLocation);
            itemStack.setItemMeta(meta);

            MessageText.COMPASS_USE.send(hunter, runnerName);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleRunnerTeleport(@NotNull PlayerTeleportEvent event) {
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        DefaultManHuntGame game = getGame(event.getPlayer().getLocation());
        if (isNullOrCreateState(game) || game.getRole(playerUniqueId) != ManHuntRole.RUNNER) {
            return;
        }

        RealEnvironment fromEnvironment = RealEnvironment.fromWorld(event.getFrom().getWorld());
        RealEnvironment toEnvironment = RealEnvironment.fromWorld(event.getTo().getWorld());

        if (fromEnvironment != toEnvironment) {
            game.getEnvironmentToRunnerLastLocation()
                    .put(fromEnvironment, ImmutableLocation.immutableCopy(event.getFrom()));
        }
    }

    @EventHandler
    public void handleRunnerQuit(@NotNull PlayerQuitEvent event) {
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        DefaultManHuntGame game = getGame(event.getPlayer().getLocation());
        if (isNullOrCreateState(game) || game.getRole(playerUniqueId) != ManHuntRole.RUNNER) {
            return;
        }

        Location playerLocation = event.getPlayer().getLocation();

        game.getEnvironmentToRunnerLastLocation()
                .put(RealEnvironment.fromWorld(playerLocation.getWorld()), ImmutableLocation.immutableCopy(playerLocation));
    }

    @EventHandler
    public void handleRunnerDeath(@NotNull PlayerDeathEvent event) {
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        DefaultManHuntGame game = getGame(event.getPlayer().getLocation());
        if (isNullOrCreateState(game) || game.getRole(playerUniqueId) != ManHuntRole.RUNNER) {
            return;
        }

        stop(game, ManHuntRole.HUNTER);
        event.getPlayer().setHealth(0);
    }

    @EventHandler
    public void handleEnderDragonDeath(@NotNull EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof EnderDragon dragon) || dragon.getDragonBattle() == null) {
            return;
        }

        DefaultManHuntGame game = getGame(entity.getLocation());
        if (isNullOrCreateState(game)) {
            return;
        }

        stop(game, ManHuntRole.RUNNER);
    }

    @EventHandler
    public void handlePlayerQuit(@NotNull PlayerQuitEvent event) {
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        DefaultManHuntGame game = getGame(event.getPlayer().getLocation());
        if (game == null || !game.isPlaying() || game.getRole(playerUniqueId) == ManHuntRole.SPECTATOR) {
            return;
        }

        if (isSafeLeave(game)) {
            handleSafeLeave(game);
        } else {
            handleNotSafeLeave(game);
        }
    }

    private boolean isSafeLeave(@NotNull DefaultManHuntGame game) {
        return config().game.safeLeave.enable &&
                System.currentTimeMillis() - (game.getStartTime() + config().game.safeLeave.enableAfter.getSeconds() * 1000) <= 0
                && Players.countOnlinePlayers(game.getPlayers()) > 1;
    }

    private void handleSafeLeave(@NotNull DefaultManHuntGame game) {
        CountDownTimer existingSafeLeaveTimer = game.getSafeLeaveTimer();
        if (existingSafeLeaveTimer != null && existingSafeLeaveTimer.isRunning()) {
            return;
        }

        CountDownTimer timer = CountDownTimer.builder()
                .times((int) config().game.safeLeave.returnDuration.getSeconds())
                .everyPeriod(left -> MessageText.END_IN.sendUniqueIds(game.getMembers(), left))
                .afterComplete(() -> {
                    MessageText.END.sendUniqueIds(game.getMembers());
                    clear(game);
                })
                .schedule();
        game.getTimers().add(timer);
        game.setSafeLeaveTimer(timer);
    }

    private void handleNotSafeLeave(@NotNull DefaultManHuntGame game) {
        MessageText.END.sendUniqueIds(game.getMembers());
        clear(game);
    }

    @EventHandler
    public void handlePlayerJoin(@NotNull PlayerJoinEvent event) {
        if (!config().game.safeLeave.enable) {
            return;
        }
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        DefaultManHuntGame game = getGame(event.getPlayer().getLocation());

        CountDownTimer safeLeaveTimer;
        if (game != null && game.isPlaying() && game.getRole(playerUniqueId) != ManHuntRole.SPECTATOR
                && (safeLeaveTimer = game.getSafeLeaveTimer()) != null) {
            safeLeaveTimer.cancel();
        }

    }

    @EventHandler
    public void handlePlayerRespawn(@NotNull PlayerRespawnEvent event) {
        if (event.isBedSpawn()) {
            return;
        }

        DefaultManHuntGame game = getGame(event.getPlayer().getLocation());
        if (game != null) {
            event.setRespawnLocation(Objects.requireNonNull(game.getSpawnLocation(), "#getSpawnLocation()").asMutable());
            log.debugIfEnabled("Relocated respawn location for {}", event.getPlayer());
        }

    }

    @Contract(value = "null -> true", pure = true)
    private static boolean isNullOrCreateState(@Nullable DefaultManHuntGame game) {
        return game == null || game.getState() == GameState.CREATE;
    }

    @Nullable
    private DefaultManHuntGame getGame(@NotNull Location location) {
        return gameRepository.find(location) instanceof DefaultManHuntGame game ? game : null;
    }
}
