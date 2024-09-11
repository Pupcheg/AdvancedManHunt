package me.supcheg.advancedmanhunt.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface FreezeGroup {
    void add(@NotNull UUID uniqueId);

    void remove(@NotNull UUID uniqueId);

    void clear();
}
