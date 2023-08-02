package me.supcheg.advancedmanhunt.game.impl;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.ManHuntGameStartEvent;
import me.supcheg.advancedmanhunt.event.ManHuntGameStopEvent;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.player.Message;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.PlayerUtil;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.timer.CountDownTimerBuilder;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.util.ThreadSafeRandom;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@CustomLog
@AllArgsConstructor
class DefaultManHuntGameService implements Listener {
    private final DefaultManHuntGameRepository gameRepository;
    private final GameRegionRepository gameRegionRepository;
    private final TemplateLoader templateLoader;
    private final CountDownTimerFactory countDownTimerFactory;
    private final PlayerReturner playerReturner;
    private final PlayerFreezer playerFreezer;
    private final EventListenerRegistry eventListenerRegistry;

    void start(@NotNull DefaultManHuntGame game, @NotNull ManHuntGameConfiguration configuration) {
        game.getState().assertIs(GameState.CREATE);

        if (!game.canStart()) {
            throw new IllegalStateException("Can't start the game without players");
        }
        game.setState(GameState.LOAD);

        // load regions
        GameRegion overworld = gameRegionRepository.getAndReserveRegion(Environment.NORMAL);
        GameRegion nether = gameRegionRepository.getAndReserveRegion(Environment.NETHER);
        GameRegion end = gameRegionRepository.getAndReserveRegion(Environment.THE_END);

        game.setOverWorldRegion(overworld);
        game.setNetherRegion(nether);
        game.setEndRegion(end);

        gameRepository.associateRegion(overworld, game);
        gameRepository.associateRegion(nether, game);
        gameRepository.associateRegion(end, game);

        templateLoader.loadTemplates(Map.of(
                overworld, configuration.getOverworldTemplate(),
                nether, configuration.getNetherTemplate(),
                end, configuration.getEndTemplate()
        )).join();

        game.setState(GameState.START);

        // randomize roles
        UUID runnerUniqueId;
        if (configuration.isRandomizeRolesOnStart()) {
            List<UUID> players = new ArrayList<>(game.getPlayers());

            UUID newRunner = ThreadSafeRandom.randomElement(players);
            players.remove(newRunner);

            game.getAllMembers().removeAll(ManHuntRole.RUNNER);
            game.getAllMembers().put(ManHuntRole.RUNNER, newRunner);
            runnerUniqueId = newRunner;

            game.getAllMembers().replaceValues(ManHuntRole.HUNTER, players);
        } else {
            runnerUniqueId = Objects.requireNonNull(game.getRunner());
        }

        Player runner = Objects.requireNonNull(Bukkit.getPlayer(runnerUniqueId), "runner");
        List<Player> onlineHunters = PlayerUtil.asPlayersList(game.getHunters());
        List<Player> onlineSpectators = PlayerUtil.asPlayersList(game.getSpectators());

        // get spawn locations
        SpawnLocationFindResult locations = configuration.getSpawnLocationFinder().find(overworld, onlineHunters.size());

        ImmutableLocation runnerLocation = locations.getRunnerLocation();
        List<ImmutableLocation> huntersLocations = locations.getHuntersLocations();
        ImmutableLocation spectatorsLocation = locations.getSpectatorsLocation();

        game.setSpawnLocation(runnerLocation);

        // Setup PortalHandler
        RegionPortalHandler portalHandler = new RegionPortalHandler(
                gameRegionRepository,
                overworld, nether, end,
                runnerLocation
        );
        eventListenerRegistry.addListener(portalHandler);
        game.setPortalHandler(portalHandler);

        // teleporting and freezing

        FreezeGroup freezeGroup = playerFreezer.newFreezeGroup();
        game.getFreezeGroups().add(freezeGroup);

        runner.teleport(runnerLocation);
        runner.getInventory().clear();
        runner.setGameMode(GameMode.ADVENTURE);
        freezeGroup.add(runner);

        ItemStack compass = new ItemStack(Material.COMPASS);
        for (int i = 0; i < onlineHunters.size(); i++) {
            Player hunter = onlineHunters.get(i);
            hunter.teleport(huntersLocations.get(i));
            hunter.setGameMode(GameMode.ADVENTURE);
            hunter.getInventory().clear();
            freezeGroup.add(hunter);
            hunter.getInventory().addItem(compass);
        }

        for (Player spectator : onlineSpectators) {
            spectator.teleport(spectatorsLocation);
            spectator.setGameMode(GameMode.SPECTATOR);
            freezeGroup.add(spectator);
        }

        newTimerBuilder(game)
                .everyPeriod((timer, left) -> Message.START_IN.sendUniqueIds(game.getMembers(), left))
                .afterComplete(timer -> {
                    Message.START.sendUniqueIds(game.getMembers());
                    PlayerUtil.forEach(game.getPlayers(), player -> player.setGameMode(GameMode.SURVIVAL));

                    freezeGroup.clear();
                    game.setState(GameState.PLAY);
                    game.setStartTime(System.currentTimeMillis());
                    new ManHuntGameStartEvent(game).callEvent();
                })
                .times(15)
                .schedule();
    }

    @NotNull
    @Contract("_ -> new")
    private CountDownTimerBuilder newTimerBuilder(@NotNull DefaultManHuntGame game) {
        return countDownTimerFactory.newBuilder().onBuild(game.getTimers()::add);
    }

    void stop(@NotNull DefaultManHuntGame game, @Nullable ManHuntRole winnerRole) {
        if (game.getState().upperOrEquals(GameState.STOP)) {
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
        if (game.getState().upper(GameState.CLEAR)) {
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

        PlayerUtil.forEach(game.getMembers(), playerReturner::returnPlayer);

        game.getOverWorldRegion().setReserved(false);
        game.getNetherRegion().setReserved(false);
        game.getEndRegion().setReserved(false);
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
                runnerLocation = game.getEnvironmentToRunnerLastLocation()
                        .get(hunter.getWorld().getEnvironment());
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

            Message.COMPASS_USE.send(hunter, runnerName);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleRunnerTeleport(@NotNull PlayerTeleportEvent event) {
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        DefaultManHuntGame game = getGame(event.getPlayer().getLocation());
        if (isNullOrCreateState(game) || game.getRole(playerUniqueId) != ManHuntRole.RUNNER) {
            return;
        }

        Environment fromEnvironment = event.getFrom().getWorld().getEnvironment();
        Environment toEnvironment = event.getTo().getWorld().getEnvironment();

        if (fromEnvironment != toEnvironment) {
            game.getEnvironmentToRunnerLastLocation()
                    .put(fromEnvironment, ImmutableLocation.copyOf(event.getFrom()));
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
                .put(playerLocation.getWorld().getEnvironment(), ImmutableLocation.copyOf(playerLocation));
    }

    @EventHandler
    public void handleRunnerDeath(@NotNull PlayerDeathEvent event) {
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        DefaultManHuntGame game = getGame(event.getPlayer().getLocation());
        if (isNullOrCreateState(game) || game.getRole(playerUniqueId) != ManHuntRole.RUNNER) {
            return;
        }

        stop(game, ManHuntRole.HUNTER);
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
        return AdvancedManHuntConfig.Game.SafeLeave.ENABLE &&
                System.currentTimeMillis() - (game.getStartTime() + AdvancedManHuntConfig.Game.SafeLeave.ENABLE_AFTER.getSeconds() * 1000) <= 0
                && PlayerUtil.countOnlinePlayers(game.getPlayers()) > 1;
    }

    private void handleSafeLeave(@NotNull DefaultManHuntGame game) {
        CountDownTimer existingSafeLeaveTimer = game.getSafeLeaveTimer();
        if (existingSafeLeaveTimer != null && existingSafeLeaveTimer.isRunning()) {
            return;
        }

        newTimerBuilder(game)
                .onBuild(game::setSafeLeaveTimer)
                .times((int) AdvancedManHuntConfig.Game.SafeLeave.RETURN_DURATION.getSeconds())
                .everyPeriod((timer, leftSeconds) -> Message.END_IN.sendUniqueIds(game.getMembers(), leftSeconds))
                .afterComplete(timer -> {
                    Message.END.sendUniqueIds(game.getMembers());
                    clear(game);
                })
                .schedule();
    }

    private void handleNotSafeLeave(@NotNull DefaultManHuntGame game) {
        Message.END.sendUniqueIds(game.getMembers());
        clear(game);
    }

    @EventHandler
    public void handlePlayerJoin(@NotNull PlayerJoinEvent event) {
        if (!AdvancedManHuntConfig.Game.SafeLeave.ENABLE) {
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
            event.setRespawnLocation(Objects.requireNonNull(game.getSpawnLocation(), "#getSpawnLocation()"));
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
