package me.supcheg.advancedmanhunt.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class Unchecked {

    private Unchecked() {
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Contract("_ -> param1")
    public static <T> T uncheckedCast(@Nullable Object o) {
        return (T) o;
    }
}
