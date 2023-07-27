package me.supcheg.advancedmanhunt.game.impl;

import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.ManHuntGameStopEvent;
import me.supcheg.advancedmanhunt.event.ManHuntGameStartEvent;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.player.Message;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.PlayerViews;
import me.supcheg.advancedmanhunt.player.freeze.FreezeGroup;
import me.supcheg.advancedmanhunt.player.freeze.PlayerFreezer;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.timer.CountDownTimerBuilder;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.random.RandomGenerator;

@AllArgsConstructor
class DefaultManHuntGameService implements Listener {
    private static final CustomLogger LOGGER = CustomLogger.getLogger(DefaultManHuntGameService.class);

    private final GameRegionRepository gameRegionRepository;
    private final TemplateLoader templateLoader;
    private final CountDownTimerFactory countDownTimerFactory;
    private final PlayerReturner playerReturner;
    private final PlayerFreezer playerFreezer;
    private final ManHuntPlayerViewRepository playerViewRepository;
    private final EventListenerRegistry eventListenerRegistry;

    void start(@NotNull DefaultManHuntGame game, @NotNull ManHuntGameConfiguration configuration) {
        game.getState().assertIs(GameState.CREATE);
        LOGGER.debugIfEnabled("Initializing game {}", this);

        if (!game.canStart()) {
            throw new IllegalStateException("Can't start the game without players");
        }
        game.setState(GameState.LOAD);

        // load regions
        LOGGER.debugIfEnabled("Loading regions");

        GameRegion overWorld = gameRegionRepository.getAndReserveRegion(Environment.NORMAL);
        GameRegion nether = gameRegionRepository.getAndReserveRegion(Environment.NETHER);
        GameRegion end = gameRegionRepository.getAndReserveRegion(Environment.THE_END);

        game.setOverWorldRegion(overWorld);
        game.setNetherRegion(nether);
        game.setEndRegion(end);

        LOGGER.debugIfEnabled("Loading templates");

        templateLoader.loadTemplates(Map.of(
                overWorld, configuration.getOverworldTemplate(),
                nether, configuration.getNetherTemplate(),
                end, configuration.getEndTemplate()
        )).join();

        game.setState(GameState.START);

        LOGGER.debugIfEnabled("Randomizing roles");

        // randomize roles
        ManHuntPlayerView runnerView;
        if (configuration.isRandomizeRolesOnStart()) {
            RandomGenerator random = new SecureRandom();

            List<ManHuntPlayerView> players = new ArrayList<>(game.getPlayers());

            ManHuntPlayerView newRunner = players.get(random.nextInt(players.size()));
            players.remove(newRunner);

            game.getAllMembers().removeAll(ManHuntRole.RUNNER);
            game.getAllMembers().put(ManHuntRole.RUNNER, newRunner);
            runnerView = newRunner;

            game.getAllMembers().replaceValues(ManHuntRole.HUNTER, players);
        } else {
            runnerView = Objects.requireNonNull(game.getRunner());
        }

        Player runner = Objects.requireNonNull(runnerView.getPlayer(), "Unreachable: runner#getPlayer() is null!");
        List<Player> onlineHunters = PlayerViews.asPlayersList(game.getHunters(), false);
        Set<Player> onlineSpectators = PlayerViews.asPlayersSet(game.getSpectators(), false);

        // get spawn locations
        LOGGER.debugIfEnabled("Searching spawn locations");
        SpawnLocationFinder spawnLocationFinder = configuration.getSpawnLocationFinder();

        Location[] huntersLocations = spawnLocationFinder.findForHunters(overWorld, onlineHunters.size());
        Location runnerLocation = spawnLocationFinder.findForRunner(overWorld);
        Location spectatorsLocation = spawnLocationFinder.findForSpectators(overWorld);

        game.setSpawnLocation(runnerLocation);

        // Setup PortalHandler
        LOGGER.debugIfEnabled("Setup PortalHandler");
        RegionPortalHandler portalHandler = new RegionPortalHandler(
                gameRegionRepository,
                overWorld, nether, end,
                runnerLocation
        );
        eventListenerRegistry.addListener(portalHandler);
        game.setPortalHandler(portalHandler);

        // teleporting and freezing
        LOGGER.debugIfEnabled("Teleporting and freezing players");

        FreezeGroup freezeGroup = playerFreezer.newFreezeGroup();
        game.getFreezeGroups().add(freezeGroup);

        runner.teleport(runnerLocation);
        runner.getInventory().clear();
        runner.setGameMode(GameMode.ADVENTURE);
        freezeGroup.add(runner);

        ItemStack compass = new ItemStack(Material.COMPASS);
        for (int i = 0; i < onlineHunters.size(); i++) {
            Player hunter = onlineHunters.get(i);
            hunter.teleport(huntersLocations[i]);
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
                .everyPeriod((timer, left) -> Message.START_IN.sendPlayerViews(game.getMembers(), left))
                .afterComplete(timer -> {
                    Message.START.sendPlayerViews(game.getMembers());
                    PlayerViews.forEach(game.getPlayers(), player -> player.setGameMode(GameMode.SURVIVAL));

                    freezeGroup.clear();
                    game.setState(GameState.PLAY);
                    game.setStartTime(System.currentTimeMillis());
                    new ManHuntGameStartEvent(game).callEvent();
                })
                .times(15)
                .schedule();

        LOGGER.debugIfEnabled("Sending messages");
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

        for (CountDownTimer timer : game.getTimers()) {
            timer.cancel();
        }
        for (FreezeGroup group : game.getFreezeGroups()) {
            group.clear();
        }

        PlayerViews.forEach(game.getMembers(), playerReturner::returnPlayer);

        for (ManHuntPlayerView member : game.getMembers()) {
            member.setGame(null);
        }

        game.getOverWorldRegion().setReserved(false);
        game.getNetherRegion().setReserved(false);
        game.getEndRegion().setReserved(false);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleHunterInteract(@NotNull PlayerInteractEvent event) {
        Player hunter = event.getPlayer();
        ManHuntPlayerView hunterView = asPlayerView(hunter);

        DefaultManHuntGame game = getGame(hunterView);
        if (game == null || game.getRole(hunterView) != ManHuntRole.HUNTER || game.getState() == GameState.CREATE) {
            return;
        }

        ItemStack itemStack = event.getItem();
        if (itemStack != null && itemStack.getType() == Material.COMPASS) {
            event.setCancelled(true);

            ManHuntPlayerView runnerView = game.getRunner();
            Objects.requireNonNull(runnerView);

            Player runner = runnerView.getPlayer();

            Location runnerLocation;
            if (runner != null) {
                runnerLocation = runner.getLocation();
            } else {
                runnerLocation = game.getEnvironmentToRunnerLastLocation()
                        .get(hunter.getWorld().getEnvironment());
            }

            if (runnerLocation == null) {
                LOGGER.error("Unreachable: runnerLocation is null! Last locations: {}", game.getEnvironmentToRunnerLastLocation());
                return;
            }

            CompassMeta meta = (CompassMeta) itemStack.getItemMeta();
            meta.setLodestoneTracked(false);
            meta.setLodestone(runnerLocation);
            itemStack.setItemMeta(meta);

            String runnerName = Objects.requireNonNull(runnerView.getOfflinePlayer().getName(), "runnerName");

            Message.COMPASS_USE.send(hunter, runnerName);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleRunnerTeleport(@NotNull PlayerTeleportEvent event) {
        ManHuntPlayerView runnerView = asPlayerView(event.getPlayer());

        DefaultManHuntGame game = getGame(runnerView);
        if (game == null || game.getRole(runnerView) != ManHuntRole.RUNNER || game.getState() == GameState.CREATE) {
            return;
        }

        Environment fromEnvironment = event.getFrom().getWorld().getEnvironment();
        Environment toEnvironment = event.getTo().getWorld().getEnvironment();

        if (fromEnvironment != toEnvironment) {
            game.getEnvironmentToRunnerLastLocation()
                    .put(fromEnvironment, event.getFrom());
        }
    }

    @EventHandler
    public void handleRunnerQuit(@NotNull PlayerQuitEvent event) {
        ManHuntPlayerView runnerView = asPlayerView(event.getPlayer());

        DefaultManHuntGame game = getGame(runnerView);
        if (game == null || game.getRole(runnerView) != ManHuntRole.RUNNER) {
            return;
        }

        Location playerLocation = event.getPlayer().getLocation();

        game.getEnvironmentToRunnerLastLocation()
                .put(playerLocation.getWorld().getEnvironment(), playerLocation);
    }

    @EventHandler
    public void handlePlayerQuit(@NotNull PlayerQuitEvent event) {
        ManHuntPlayerView playerView = asPlayerView(event.getPlayer());

        DefaultManHuntGame game = getGame(playerView);
        if (game == null || !game.isPlaying() || game.getRole(playerView) == ManHuntRole.SPECTATOR) {
            return;
        }

        boolean isSafeLeave = AdvancedManHuntConfig.Game.SafeLeave.ENABLE &&
                System.currentTimeMillis() - (game.getStartTime() + AdvancedManHuntConfig.Game.SafeLeave.ENABLE_AFTER.getSeconds() * 1000) <= 0;
        LOGGER.debugIfEnabled("Handling quit event for {}. Is safe leave: {}", playerView, isSafeLeave);

        if (isSafeLeave) {
            Set<Player> onlinePlayers = PlayerViews.asPlayersSet(game.getPlayers(), false);
            if (onlinePlayers.size() == 1) {
                clear(game);
                return;
            }

            newTimerBuilder(game)
                    .onBuild(game::setSafeLeaveTimer)
                    .times((int) AdvancedManHuntConfig.Game.SafeLeave.RETURN_DURATION.getSeconds())
                    .everyPeriod((timer, leftSeconds) -> Message.END_IN.sendPlayerViews(game.getMembers(), leftSeconds))
                    .afterComplete(timer -> {
                        Message.END.sendPlayerViews(game.getMembers());
                        clear(game);
                    })
                    .schedule();
        } else {
            Message.END.sendPlayerViews(game.getMembers());
            clear(game);
        }
    }

    @EventHandler
    public void handlePlayerJoin(@NotNull PlayerJoinEvent event) {
        if (!AdvancedManHuntConfig.Game.SafeLeave.ENABLE) {
            return;
        }

        ManHuntPlayerView playerView = asPlayerView(event.getPlayer());

        DefaultManHuntGame game = getGame(playerView);
        if (game != null && game.isPlaying() && game.getRole(playerView) != ManHuntRole.SPECTATOR) {
            game.getSafeLeaveTimer().cancel();
        }

    }

    @EventHandler
    public void handlePlayerRespawn(@NotNull PlayerRespawnEvent event) {
        if (event.isBedSpawn()) {
            return;
        }

        ManHuntPlayerView playerView = asPlayerView(event.getPlayer());

        DefaultManHuntGame game = getGame(playerView);
        if (game != null) {
            LOGGER.debugIfEnabled("Relocating respawn location for {}", playerView);
            event.setRespawnLocation(Objects.requireNonNull(game.getSpawnLocation(), "#getSpawnLocation()"));
        }

    }

    @NotNull
    private ManHuntPlayerView asPlayerView(@NotNull Player player) {
        return playerViewRepository.get(player);
    }

    @Nullable
    private DefaultManHuntGame getGame(@NotNull ManHuntPlayerView playerView) {
        ManHuntGame game = playerView.getGame();
        return game instanceof DefaultManHuntGame defaultManHuntGame ? defaultManHuntGame : null;
    }
}
