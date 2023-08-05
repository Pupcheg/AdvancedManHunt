package me.supcheg.advancedmanhunt.game.impl;

import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultManHuntGameRepository implements ManHuntGameRepository {
    private final GameRegionRepository gameRegionRepository;
    private final DefaultManHuntGameService gameService;
    private final Map<UUID, ManHuntGame> uniqueId2game;
    private final Map<GameRegion, ManHuntGame> gameRegion2game;
    private final Collection<ManHuntGame> unmodifiableGames;

    public DefaultManHuntGameRepository(@NotNull GameRegionRepository gameRegionRepository,
                                        @NotNull TemplateLoader templateLoader,
                                        @NotNull CountDownTimerFactory countDownTimerFactory,
                                        @NotNull PlayerReturner playerReturner, @NotNull PlayerFreezer playerFreezer,
                                        @NotNull EventListenerRegistry eventListenerRegistry) {
        this.gameRegionRepository = gameRegionRepository;
        this.gameService = new DefaultManHuntGameService(this, gameRegionRepository, templateLoader,
                countDownTimerFactory, playerReturner, playerFreezer, eventListenerRegistry);
        this.uniqueId2game = new HashMap<>();
        this.gameRegion2game = new HashMap<>();
        this.unmodifiableGames = Collections.unmodifiableCollection(uniqueId2game.values());

        eventListenerRegistry.addListener(gameService);
    }

    @NotNull
    @Override
    public ManHuntGame create(@NotNull UUID owner, int maxHunters, int maxSpectators) {
        UUID uniqueId = newUniqueId();
        ManHuntGame game = new DefaultManHuntGame(gameService, uniqueId, owner, maxHunters, maxSpectators);
        uniqueId2game.put(uniqueId, game);
        return game;
    }

    @Override
    public void invalidate(@NotNull ManHuntGame game) {
        uniqueId2game.remove(game.getUniqueId());
        gameRegion2game.remove(game.getOverWorldRegion());
        gameRegion2game.remove(game.getNetherRegion());
        gameRegion2game.remove(game.getEndRegion());
    }

    @NotNull
    private UUID newUniqueId() {
        UUID uniqueId;
        do {
            uniqueId = UUID.randomUUID();
        } while (uniqueId2game.containsKey(uniqueId));

        return uniqueId;
    }

    @Nullable
    @Override
    public ManHuntGame find(@NotNull UUID uniqueId) {
        return uniqueId2game.get(uniqueId);
    }

    @Nullable
    @Override
    public ManHuntGame find(@NotNull Location location) {
        GameRegion gameRegion = gameRegionRepository.findRegion(location);
        return gameRegion == null ? null : gameRegion2game.get(gameRegion);
    }

    void associateRegion(@NotNull GameRegion gameRegion, @NotNull ManHuntGame game) {
        gameRegion2game.put(gameRegion, game);
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Collection<ManHuntGame> getManHuntGames() {
        return unmodifiableGames;
    }
}
