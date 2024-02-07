package me.supcheg.advancedmanhunt.game.impl;

import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.ManHuntGameCreateEvent;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import me.supcheg.advancedmanhunt.storage.InMemoryEntityRepository;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.util.ThreadSafeRandom;
import me.supcheg.advancedmanhunt.util.concurrent.FuturesBuilderFactory;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultManHuntGameRepository extends InMemoryEntityRepository<ManHuntGame, UUID> implements ManHuntGameRepository {
    private final GameRegionRepository gameRegionRepository;
    private final DefaultManHuntGameService gameService;
    private final Map<GameRegion, ManHuntGame> gameRegion2game;

    public DefaultManHuntGameRepository(@NotNull GameRegionRepository gameRegionRepository,
                                        @NotNull EntityRepository<Template, String> templateRepository,
                                        @NotNull TemplateLoader templateLoader,
                                        @NotNull CountDownTimerFactory countDownTimerFactory,
                                        @NotNull PlayerReturner playerReturner, @NotNull PlayerFreezer playerFreezer,
                                        @NotNull EventListenerRegistry eventListenerRegistry,
                                        @NotNull FuturesBuilderFactory futuresBuilderFactory,
                                        @NotNull AdvancedGuiController guiController) {
        super(ManHuntGame::getUniqueId);
        this.gameRegionRepository = gameRegionRepository;
        this.gameService = new DefaultManHuntGameService(this, gameRegionRepository, templateRepository, templateLoader,
                countDownTimerFactory, playerReturner, playerFreezer, eventListenerRegistry, futuresBuilderFactory, guiController);
        this.gameRegion2game = new HashMap<>();

        eventListenerRegistry.addListener(gameService);
    }

    @NotNull
    @Override
    public ManHuntGame create(@NotNull UUID owner) {
        UUID uniqueId = newUniqueId();
        ManHuntGame game = new DefaultManHuntGame(gameService, uniqueId, owner);
        storeEntity(game);
        new ManHuntGameCreateEvent(game).callEvent();
        return game;
    }

    @Override
    public boolean invalidateKey(@NotNull UUID key) {
        ManHuntGame game = entities.remove(key);
        if (game != null) {
            disassociateRegions(game);
            return true;
        }
        return false;
    }

    @NotNull
    private UUID newUniqueId() {
        UUID uniqueId;
        do {
            uniqueId = ThreadSafeRandom.randomUniqueId();
        } while (containsKey(uniqueId));

        return uniqueId;
    }

    @Nullable
    @Override
    public ManHuntGame find(@NotNull Location location) {
        GameRegion gameRegion = gameRegionRepository.findRegion(location);
        return gameRegion == null ? null : gameRegion2game.get(gameRegion);
    }

    void disassociateRegions(@NotNull ManHuntGame game) {
        gameRegion2game.remove(game.getOverWorldRegion());
        gameRegion2game.remove(game.getNetherRegion());
        gameRegion2game.remove(game.getEndRegion());
    }

    void associateRegion(@NotNull GameRegion gameRegion, @NotNull ManHuntGame game) {
        gameRegion2game.put(gameRegion, game);
    }
}
