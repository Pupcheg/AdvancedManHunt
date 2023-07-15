package me.supcheg.advancedmanhunt.game.impl;

import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.freeze.PlayerFreezer;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class DefaultManHuntGameRepository implements ManHuntGameRepository {
    private final DefaultManHuntGameService gameService;
    private final Map<UUID, ManHuntGame> uniqueId2game;
    private final Collection<ManHuntGame> unmodifiableGames;

    public DefaultManHuntGameRepository(@NotNull GameRegionRepository gameRegionRepository,
                                        @NotNull TemplateLoader templateLoader,
                                        @NotNull CountDownTimerFactory countDownTimerFactory,
                                        @NotNull PlayerReturner playerReturner, @NotNull PlayerFreezer playerFreezer,
                                        @NotNull ManHuntPlayerViewRepository manHuntPlayerViewRepository,
                                        @NotNull EventListenerRegistry eventListenerRegistry) {
        this.gameService = new DefaultManHuntGameService(gameRegionRepository, templateLoader, countDownTimerFactory,
                playerReturner, playerFreezer, manHuntPlayerViewRepository, eventListenerRegistry);
        this.uniqueId2game = new HashMap<>();
        this.unmodifiableGames = Collections.unmodifiableCollection(uniqueId2game.values());

        eventListenerRegistry.addListener(gameService);
    }

    @Override
    @NotNull
    public ManHuntGame create(@NotNull ManHuntPlayerView owner, int maxHunters, int maxSpectators) {
        UUID uniqueId = newUniqueId();
        ManHuntGame game = new DefaultManHuntGame(gameService, uniqueId, owner, maxHunters, maxSpectators);
        uniqueId2game.put(uniqueId, game);
        return game;
    }

    @NotNull
    private UUID newUniqueId() {
        UUID uniqueId;
        do {
            uniqueId = UUID.randomUUID();
        } while (uniqueId2game.containsKey(uniqueId));

        return uniqueId;
    }

    @Override
    @Nullable
    public ManHuntGame find(@NotNull UUID uniqueId) {
        return uniqueId2game.get(uniqueId);
    }

    @Override
    @NotNull
    @UnmodifiableView
    public Collection<ManHuntGame> getManHuntGames() {
        return unmodifiableGames;
    }
}
