package me.supcheg.advancedmanhunt.game.impl;

import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.config.SoundsConfig;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.PlayerViews;
import me.supcheg.advancedmanhunt.player.freeze.FreezeGroup;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.timer.CountDownTimerBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

class DefaultManHuntGameService implements Listener {

    private final AdvancedManHuntPlugin plugin;
    private final CustomLogger logger;

    public DefaultManHuntGameService(@NotNull AdvancedManHuntPlugin plugin) {
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

        GameRegion overWorld = regionRepository.getAndReserveRegion(World.Environment.NORMAL);
        GameRegion nether = regionRepository.getAndReserveRegion(World.Environment.NETHER);
        GameRegion end = regionRepository.getAndReserveRegion(World.Environment.THE_END);

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

            List<ManHuntPlayerView> players = game.getPlayers();

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

        runner.teleport(runnerLocation);
        runner.getInventory().clear();
        freezeGroup.add(runner);

        ItemStack compass = new ItemStack(Material.COMPASS);
        for (int i = 0; i < onlineHunters.size(); i++) {
            Player hunter = onlineHunters.get(i);
            hunter.teleport(huntersLocations[i]);
            hunter.getInventory().clear();
            freezeGroup.add(hunter);
            hunter.getInventory().addItem(compass);
        }

        for (Player spectator : onlineSpectators) {
            spectator.teleport(spectatorsLocation);
            freezeGroup.add(spectator);
        }

        newTimerBuilder(game)
                .everyPeriod((timer, left) -> {
                            for (ManHuntPlayerView member : game.getMembers()) {
                                member.sendMessage(Component.translatable("game.start.countdown", Component.text(left)));
                                member.playSound(SoundsConfig.GAME_START_COUNTDOWN);
                            }
                        }
                )
                .afterComplete(timer -> {
                    for (ManHuntPlayerView member : game.getMembers()) {
                        member.sendMessage(Component.translatable("game.start"));
                        member.playSound(SoundsConfig.GAME_START);
                    }
                    freezeGroup.clear();
                    game.setState(GameState.PLAY);
                    game.setStartTime(System.currentTimeMillis());
                })
                .period(1)
                .times(15)
                .schedule();

        logger.debugIfEnabled("Sending messages");

        Component runnerComponent = Component.text("Runner: " + runner.getName(), TextColor.color(0x65FF87));
        Component huntersComponent = Component.text("Hunters: " + onlineHunters.stream()
                        .map(Player::getName)
                        .collect(Collectors.joining(", ")),
                TextColor.color(0xFF502E));
        Component spectatorsComponent = Component.text("Spectators: " + onlineSpectators.stream()
                        .map(Player::getName)
                        .collect(Collectors.joining(", ")),
                TextColor.color(0xEEFF44));

        for (ManHuntPlayerView member : game.getMembers()) {
            member.sendMessage(runnerComponent);
            member.sendMessage(huntersComponent);
            member.sendMessage(spectatorsComponent);
        }
    }

    @NotNull
    @Contract("_ -> new")
    private CountDownTimerBuilder newTimerBuilder(@NotNull DefaultManHuntGame game) {
        return plugin.getCountDownTimerFactory()
                .newBuilder()
                .onBuild(game.getTimers()::add);
    }

    void stop(@NotNull DefaultManHuntGame game) {
        for (CountDownTimer timer : game.getTimers()) {
            timer.cancel();
        }

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
        if (game == null || hunterView.getRole() != ManHuntRole.HUNTER || game.getState() == GameState.CREATE) {
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

            hunter.playSound(SoundsConfig.HUNTER_COMPASS_USE);
            hunter.sendMessage(Component.translatable("hunter.compass.use", Component.text(runnerName)));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleRunnerTeleport(@NotNull PlayerTeleportEvent event) {
        ManHuntPlayerView runnerView = asPlayerView(event.getPlayer());

        DefaultManHuntGame game = getGame(runnerView);
        if (game == null || runnerView.getRole() != ManHuntRole.RUNNER || game.getState() == GameState.CREATE) {
            return;
        }

        World.Environment fromEnvironment = event.getFrom().getWorld().getEnvironment();
        World.Environment toEnvironment = event.getTo().getWorld().getEnvironment();

        if (fromEnvironment != toEnvironment) {
            game.getEnvironmentToRunnerLastLocation()
                    .put(fromEnvironment, event.getFrom());
        }
    }

    @EventHandler
    public void handleRunnerQuit(@NotNull PlayerQuitEvent event) {
        ManHuntPlayerView runnerView = asPlayerView(event.getPlayer());

        DefaultManHuntGame game = (DefaultManHuntGame) runnerView.getGame();
        if (game == null || runnerView.getRole() != ManHuntRole.RUNNER) {
            return;
        }

        var playerLocation = event.getPlayer().getLocation();

        game.getEnvironmentToRunnerLastLocation()
                .put(playerLocation.getWorld().getEnvironment(), playerLocation);
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
        World.Environment fromEnvironment = fromLocation.getWorld().getEnvironment();
        GameRegion fromRegion = game.getRegion(fromEnvironment);

        Location toLocation = event.getTo();
        World.Environment toEnvironment = toLocation.getWorld().getEnvironment();
        GameRegion toRegion = game.getRegion(toEnvironment);

        fromRegion.removeDelta(fromLocation);


    }

    @NotNull
    private ManHuntPlayerView asPlayerView(@NotNull Player player) {
        return plugin.getPlayerViewRepository().get(player);
    }

    @Nullable
    private DefaultManHuntGame getGame(@NotNull ManHuntPlayerView playerView) {
        return (DefaultManHuntGame) playerView.getGame();
    }
}
