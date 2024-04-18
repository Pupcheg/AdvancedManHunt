package me.supcheg.advancedmanhunt.game;

import com.google.common.collect.Collections2;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.action.Action;
import me.supcheg.advancedmanhunt.action.ActionExecutor;
import me.supcheg.advancedmanhunt.action.ActionThrowable;
import me.supcheg.advancedmanhunt.action.DefaultActionExecutor;
import me.supcheg.advancedmanhunt.action.RunningAction;
import me.supcheg.advancedmanhunt.command.exception.CustomExceptions;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.event.ManHuntGameCreateEvent;
import me.supcheg.advancedmanhunt.event.ManHuntGameStartEvent;
import me.supcheg.advancedmanhunt.game.handler.ManHuntGameCompassHandler;
import me.supcheg.advancedmanhunt.game.handler.ManHuntGameConfigHandler;
import me.supcheg.advancedmanhunt.game.handler.ManHuntGameStopHandler;
import me.supcheg.advancedmanhunt.game.handler.RegionPortalHandler;
import me.supcheg.advancedmanhunt.game.handler.SafeLeaveHandler;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.player.Permission;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.Players;
import me.supcheg.advancedmanhunt.random.ThreadSafeRandom;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateService;
import me.supcheg.advancedmanhunt.text.MessageText;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static me.supcheg.advancedmanhunt.action.Action.anyThread;
import static me.supcheg.advancedmanhunt.action.Action.join;
import static me.supcheg.advancedmanhunt.action.Action.mainThread;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.player.Players.asPlayersView;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ManHuntGameService {
    private final ManHuntGameRepository gameRepository;
    private final GameRegionRepository regionRepository;
    private final TemplateService templateService;
    private final PlayerReturner playerReturner;
    private final PlayerFreezer playerFreezer;
    private final AdvancedGuiController guiController;
    private final ActionExecutor actionExecutor = new DefaultActionExecutor(
            BukkitUtil.mainThreadExecutor(),
            Executors.newFixedThreadPool(2)
    );

    @Nullable
    public ManHuntGame getGame(@NotNull UUID uniqueId) {
        return gameRepository.getEntity(uniqueId);
    }

    @CanIgnoreReturnValue
    @NotNull
    public ManHuntGame createGame(@NotNull UUID ownerUniqueId) {
        UUID uniqueId = newUniqueId();
        ManHuntGame game = new ManHuntGame(uniqueId, ownerUniqueId);
        game.registerHandler(g -> new ManHuntGameConfigHandler(g, guiController));

        gameRepository.storeEntity(game);

        new ManHuntGameCreateEvent(game).callEvent();

        return game;
    }

    @NotNull
    @Contract("_ -> new")
    public RunningAction start(@NotNull ManHuntGame game) {
        return new StartManHuntGameRunnable(game).execute();
    }

    @RequiredArgsConstructor
    private class StartManHuntGameRunnable {
        private final ManHuntGame game;

        private Template overworldTemplate;
        private Template netherTemplate;
        private Template endTemplate;

        private ImmutableLocation runnerLocation;
        private List<ImmutableLocation> huntersLocations;
        private ImmutableLocation spectatorsLocation;

        private FreezeGroup freezeGroup;
        private CountDownTimer startTimer;

        @NotNull
        public RunningAction execute() {
            Action action = join(
                    anyThread("assert_can_start")
                            .execute(() -> {
                                if (game.getState() != GameState.CREATE) {
                                    throw new IllegalStateException("Game is not at the CREATE state");
                                }

                                if (Players.areAllOffline(game.getRunnerAsCollection())
                                        || Players.areAllOffline(game.getHunters())) {
                                    throw new IllegalStateException("Can't start the game without players");
                                }
                            }),
                    anyThread("set_load_state")
                            .execute(() -> game.setState(GameState.LOAD))
                            .discard(() -> game.setState(GameState.CREATE)),
                    mainThread("freeze_config")
                            .execute(() -> {
                                game.unregisterHandler(ManHuntGameConfigHandler.class);
                                game.getConfig().freeze();
                            })
                            .discard(() -> {
                                game.getConfig().unfreeze();
                                game.registerHandler(game -> new ManHuntGameConfigHandler(game, guiController));
                            }),
                    mainThread("load_regions")
                            .execute(() -> {
                                game.setOverworld(regionRepository.getAndReserveRegion(RealEnvironment.OVERWORLD));
                                game.setNether(regionRepository.getAndReserveRegion(RealEnvironment.NETHER));
                                game.setEnd(regionRepository.getAndReserveRegion(RealEnvironment.THE_END));
                            })
                            .discard(() -> {
                                setNotReservedIfNonNull(game.getOverworld());
                                game.setOverworld(null);

                                setNotReservedIfNonNull(game.getNether());
                                game.setNether(null);

                                setNotReservedIfNonNull(game.getEnd());
                                game.setEnd(null);
                            }),
                    anyThread("find_templates")
                            .execute(() -> {
                                overworldTemplate = templateService.getTemplateOrThrow(game.getConfig().getOverworldTemplate());
                                netherTemplate = templateService.getTemplateOrThrow(game.getConfig().getNetherTemplate());
                                endTemplate = templateService.getTemplateOrThrow(game.getConfig().getEndTemplate());
                            })
                            .discard(() -> {
                                overworldTemplate = null;
                                netherTemplate = null;
                                endTemplate = null;
                            }),
                    mainThread("unload_regions")
                            .execute(() -> {
                                boolean notUnloaded = !game.getOverworld().unload()
                                        || !game.getNether().unload()
                                        || !game.getEnd().unload();
                                if (notUnloaded) {
                                    throw new IllegalStateException("Can't unload regions for " + game);
                                }
                            }),
                    anyThread("load_templates")
                            .execute(() ->
                                    CompletableFuture.allOf(
                                            templateService.loadTemplate(game.getOverworld(), overworldTemplate),
                                            templateService.loadTemplate(game.getNether(), netherTemplate),
                                            templateService.loadTemplate(game.getEnd(), endTemplate)
                                    ).join()
                            ),
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
                                game.setSpawnLocation(null);
                            }),
                    mainThread("setup_compass_handler")
                            .execute(() -> game.registerHandler(ManHuntGameCompassHandler::new))
                            .discard(() -> game.unregisterHandler(ManHuntGameCompassHandler.class)),
                    mainThread("setup_stop_handler")
                            .execute(() -> game.registerHandler(g -> new ManHuntGameStopHandler(g, playerReturner)))
                            .discard(() -> game.unregisterHandler(ManHuntGameStopHandler.class)),
                    mainThread("setup_region_portal_handler")
                            .execute(() -> game.registerHandler(RegionPortalHandler::new))
                            .discard(() -> game.unregisterHandler(RegionPortalHandler.class)),
                    mainThread("setup_safe_leave_handler")
                            .execute(() -> {
                                if (config().game.safeLeave.enable) {
                                    game.registerHandler(SafeLeaveHandler::new);
                                }
                            })
                            .discard(() -> game.unregisterHandler(SafeLeaveHandler.class)),
                    anyThread("freeze_players")
                            .execute(() -> {
                                freezeGroup = playerFreezer.newFreezeGroup();
                                game.getMembers().forEach(freezeGroup::add);
                                game.getFreezeGroups().add(freezeGroup);
                            })
                            .discard(() -> {
                                if (freezeGroup != null) {
                                    freezeGroup.clear();
                                    game.getFreezeGroups().remove(freezeGroup);
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
                                for (Player hunter : asPlayersView(game.getHunters())) {
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
                                        startTimer = CountDownTimer.times(15)
                                                .everyPeriod(left -> MessageText.START_IN.sendUniqueIds(game.getMembers(), left))
                                                .afterComplete(() -> {
                                                    MessageText.START.sendUniqueIds(game.getMembers());
                                                    Players.forEach(game.getPlayers(),
                                                            player -> player.setGameMode(GameMode.SURVIVAL)
                                                    );

                                                    freezeGroup.clear();
                                                    game.setState(GameState.PLAY);
                                                    new ManHuntGameStartEvent(game).callEvent();
                                                })
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

            RunningAction runningAction = actionExecutor.execute(action);
            runningAction.asCompletableFuture()
                    .thenApply(act -> {
                        List<ActionThrowable> throwables = act.listThrowables();
                        for (ActionThrowable thr : throwables) {
                            log.error("An error occurred while starting {}, action_key='{}'",
                                    game, thr.getAction().name(), thr.getThrowable()
                            );
                        }
                        return throwables.isEmpty();
                    });
            return runningAction;
        }

        private static void setNotReservedIfNonNull(@Nullable GameRegion region) {
            if (region != null) {
                region.setReserved(false);
            }
        }
    }

    @NotNull
    public Collection<ManHuntGame> getAllGames() {
        return gameRepository.getEntities();
    }

    @NotNull
    public Collection<String> getStringKeys() {
        return Collections2.transform(gameRepository.getKeys(), UUID::toString);
    }

    public void assertCanConfigure(@NotNull CommandSender sender, @NotNull ManHuntGame game) throws CommandSyntaxException {
        if (sender instanceof Player player && !canConfigure(player, game)) {
            throw CustomExceptions.ACCESS_DENIED.create();
        }
    }

    public boolean canConfigure(@NotNull Player player, @NotNull ManHuntGame game) {
        return game.getOwner().equals(player.getUniqueId()) || player.hasPermission(Permission.CONFIGURE_ANY_GAME);
    }

    @NotNull
    private UUID newUniqueId() {
        UUID uniqueId;
        do {
            uniqueId = ThreadSafeRandom.randomUniqueId();
        } while (gameRepository.containsKey(uniqueId));

        return uniqueId;
    }
}
