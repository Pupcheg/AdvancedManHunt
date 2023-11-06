package me.supcheg.advancedmanhunt.game;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
public enum ManHuntRole {
    RUNNER(game -> game.getRunner() == null, game -> Collections.singleton(game.getRunner())),
    HUNTER(game -> game.getHunters().size() < game.getConfig().getMaxHunters(), ManHuntGame::getHunters),
    SPECTATOR(game -> game.getSpectators().size() < game.getConfig().getMaxSpectators(), ManHuntGame::getSpectators);

    public static final List<ManHuntRole> VALUES = List.of(values());

    private final Predicate<ManHuntGame> joinPredicate;
    private final Function<ManHuntGame, Collection<UUID>> playersFunction;

    public boolean canJoin(@NotNull ManHuntGame game) {
        return joinPredicate.test(game);
    }

    @NotNull
    public Collection<UUID> getPlayers(@NotNull ManHuntGame game) {
        return playersFunction.apply(game);
    }
}
