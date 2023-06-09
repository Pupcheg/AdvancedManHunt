package me.supcheg.advancedmanhunt.game.impl;

import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.Message;
import me.supcheg.advancedmanhunt.player.PlayerViews;
import me.supcheg.advancedmanhunt.player.freeze.FreezeGroup;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.timer.CountDownTimerBuilder;
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
import org.bukkit.event.player.PlayerPortalEvent;
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

class DefaultManHuntGameService implements Listener {

    private final AdvancedManHuntPlugin plugin;
    private final CustomLogger logger;

    DefaultManHuntGameService(@NotNull AdvancedManHuntPlugin plugin) {
        this.plugin = plugin;
        plugin.addListener(this);
        this.logger = plugin.getSLF4JLogger().newChild(DefaultManHuntGameService.class);
    }

    void start(@NotNull DefaultManHuntGame game, @NotNull ManHuntGameConfiguration configuration) {
        game.getState().assertIs(GameState.CREATE);
        logger.debugIfEnabled("Initializing game {}", this);

        if (!game.canStart()) {
            throw new IllegalStateException("Can't start the game without players");
        }
        game.setState(GameState.LOAD);

        // load regions
        logger.debugIfEnabled("Loading regions");
        GameRegionRepository regionRepository = plugin.getGameRegionRepository();

        GameRegion overWorld = regionRepository.getAndReserveRegion(Environment.NORMAL);
        GameRegion nether = regionRepository.getAndReserveRegion(Environment.NETHER);
        GameRegion end = regionRepository.getAndReserveRegion(Environment.THE_END);

        game.setOverWorldRegion(overWorld);
        game.setNetherRegion(nether);
        game.setEndRegion(end);

        logger.debugIfEnabled("Loading templates");

        plugin.getTemplateLoader().loadTemplates(Map.of(
                overWorld, configuration.getOverworldTemplate(),
                nether, configuration.getNetherTemplate(),
                end, configuration.getEndTemplate()
        )).join();

        game.setState(GameState.START);

        logger.debugIfEnabled("Randomizing roles");

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
        logger.debugIfEnabled("Searching spawn locations");
        SpawnLocationFinder spawnLocationFinder = configuration.getSpawnLocationFinder();

        Location[] huntersLocations = spawnLocationFinder.findForHunters(overWorld, onlineHunters.size());
        Location runnerLocation = spawnLocationFinder.findForRunner(overWorld);
        Location spectatorsLocation = spawnLocationFinder.findForSpectators(overWorld);

        game.setSpawnLocation(runnerLocation);

        // associate this game to players
        for (ManHuntPlayerView member : game.getMembers()) {
            member.setGame(game);
        }

        // teleporting and freezing
        logger.debugIfEnabled("Teleporting and freezing players");

        FreezeGroup freezeGroup = plugin.getPlayerFreezer().newFreezeGroup();
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
                })
                .times(15)
                .schedule();

        logger.debugIfEnabled("Sending messages");
    }

    @NotNull
    @Contract("_ -> new")
    private CountDownTimerBuilder newTimerBuilder(@NotNull DefaultManHuntGame game) {
        return plugin.getCountDownTimerFactory()
                .newBuilder()
                .onBuild(game.getTimers()::add);
    }

    void stop(@NotNull DefaultManHuntGame game, @Nullable ManHuntRole winnerRole) {
        if (!game.getState().upperOrEquals(GameState.STOP)) {
            throw new IllegalStateException("The game has already been stopped or is in the process of clearing");
        }

        if (winnerRole == ManHuntRole.SPECTATOR) {
            throw new IllegalArgumentException("Available parameters are %s, %s or null"
                    .formatted(ManHuntRole.RUNNER, ManHuntRole.HUNTER));
        }

        game.setState(GameState.STOP);
        // TODO: 30.05.2023 add pretty game stop

        clear(game);
    }

    void clear(@NotNull DefaultManHuntGame game) {
        if (game.getState().upperOrEquals(GameState.CLEAR)) {
            throw new IllegalStateException("The game is already in the process of being cleaned up");
        }
        game.setState(GameState.CLEAR);

        for (CountDownTimer timer : game.getTimers()) {
            timer.cancel();
        }
        for (FreezeGroup group : game.getFreezeGroups()) {
            group.clear();
        }

        PlayerViews.forEach(game.getMembers(), plugin.getPlayerReturner()::returnPlayer);

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
                logger.error("Unreachable: runnerLocation is null! Last locations: {}", game.getEnvironmentToRunnerLastLocation());
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

        boolean isSafeLeave;
        if (AdvancedManHuntConfig.Game.SafeLeave.ENABLE) {
            long safeLeaveStartTime = game.getStartTime() + AdvancedManHuntConfig.Game.SafeLeave.ENABLE_AFTER.getSeconds() * 1000;
            isSafeLeave = System.currentTimeMillis() - safeLeaveStartTime < 0;
        } else {
            isSafeLeave = false;
        }
        logger.debugIfEnabled("Handling quit event for {}. Is safe leave: {}", playerView, isSafeLeave);

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
            logger.debugIfEnabled("Relocating respawn location for {}", playerView);
            event.setRespawnLocation(game.getSpawnLocation());
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPortal(@NotNull PlayerPortalEvent event) {
        ManHuntPlayerView playerView = plugin.getPlayerViewRepository().get(event.getPlayer());
        ManHuntGame game = playerView.getGame();

        if (game == null) {
            return;
        }

        if (game.getState() != GameState.PLAY) {
            event.setCancelled(true);
            return;
        }

        Location fromLocation = event.getFrom();
        Environment fromEnvironment = fromLocation.getWorld().getEnvironment();
        GameRegion fromRegion = game.getRegion(fromEnvironment);

        Location toLocation = event.getTo();
        Environment toEnvironment = toLocation.getWorld().getEnvironment();
        GameRegion toRegion = game.getRegion(toEnvironment);

        fromRegion.removeDelta(fromLocation);


    }

    @NotNull
    private ManHuntPlayerView asPlayerView(@NotNull Player player) {
        return plugin.getPlayerViewRepository().get(player);
    }

    @Nullable
    private DefaultManHuntGame getGame(@NotNull ManHuntPlayerView playerView) {
        ManHuntGame game = playerView.getGame();
        return game instanceof DefaultManHuntGame defaultManHuntGame ? defaultManHuntGame : null;
    }
}
