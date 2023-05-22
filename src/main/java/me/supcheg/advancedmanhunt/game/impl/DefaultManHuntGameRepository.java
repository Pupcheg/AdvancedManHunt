package me.supcheg.advancedmanhunt.game.impl;

import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultManHuntGameRepository implements ManHuntGameRepository {
    private final DefaultManHuntGameService gameService;
    private final Map<UUID, ManHuntGame> uniqueId2game;
    private final Collection<ManHuntGame> unmodifiableGames;

    public DefaultManHuntGameRepository(@NotNull AdvancedManHuntPlugin plugin) {
        this.gameService = new DefaultManHuntGameService(plugin);
        this.uniqueId2game = new HashMap<>();
        this.unmodifiableGames = Collections.unmodifiableCollection(uniqueId2game.values());
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
