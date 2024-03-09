package me.supcheg.advancedmanhunt.game;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public enum ManHuntRole {
    RUNNER {
        @Override
        public boolean canJoin(@NotNull ManHuntGame game) {
            return game.getRunner() == null;
        }

        @NotNull
        @Override
        public Collection<UUID> getPlayers(@NotNull ManHuntGame game) {
            return Collections.singleton(game.getRunner());
        }
    },
    HUNTER {
        @Override
        public boolean canJoin(@NotNull ManHuntGame game) {
            return game.getHunters().size() < game.getConfig().getMaxHunters();
        }

        @NotNull
        @Override
        public Collection<UUID> getPlayers(@NotNull ManHuntGame game) {
            return game.getHunters();
        }
    },
    SPECTATOR {
        @Override
        public boolean canJoin(@NotNull ManHuntGame game) {
            return game.getSpectators().size() < game.getConfig().getMaxSpectators();
        }

        @NotNull
        @Override
        public Collection<UUID> getPlayers(@NotNull ManHuntGame game) {
            return game.getSpectators();
        }
    };

    private static final List<ManHuntRole> VALUES = List.of(values());

    @NotNull
    @Contract(pure = true)
    public static List<ManHuntRole> allManHuntRoles() {
        return VALUES;
    }

    public abstract boolean canJoin(@NotNull ManHuntGame game);

    @NotNull
    public abstract Collection<UUID> getPlayers(@NotNull ManHuntGame game);
}
