package me.supcheg.advancedmanhunt.game.impl;

import me.supcheg.advancedmanhunt.event.ManHuntGameCreateEvent;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.random.ThreadSafeRandom;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.storage.InMemoryEntityRepository;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DefaultManHuntGameRepository extends InMemoryEntityRepository<ManHuntGame, UUID> implements ManHuntGameRepository {
    private final DefaultManHuntGameService gameService;

    public DefaultManHuntGameRepository(@NotNull GameRegionRepository gameRegionRepository,
                                        @NotNull TemplateRepository templateRepository,
                                        @NotNull TemplateLoader templateLoader,
                                        @NotNull PlayerReturner playerReturner,
                                        @NotNull PlayerFreezer playerFreezer,
                                        @NotNull AdvancedGuiController guiController) {
        super(ManHuntGame::getUniqueId);
        this.gameService = new DefaultManHuntGameService(this, gameRegionRepository, templateRepository, templateLoader,
                playerReturner, playerFreezer, guiController);

        BukkitUtil.registerEventListener(gameService);
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
        RealEnvironment environment = RealEnvironment.fromWorld(location.getWorld());
        for (ManHuntGame game : entities.values()) {
            GameRegion region = game.getRegion(environment);
            if (region != null && region.contains(location)) {
                return game;
            }
        }
        return null;
    }
}
