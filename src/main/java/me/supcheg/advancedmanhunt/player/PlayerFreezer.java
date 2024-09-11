package me.supcheg.advancedmanhunt.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerFreezer {

    boolean isFrozen(@NotNull UUID uniqueId);

    @NotNull
    FreezeGroup newFreezeGroup();
}
