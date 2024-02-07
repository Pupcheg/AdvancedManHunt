package me.supcheg.advancedmanhunt.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Unchecked {
    @Nullable
    @Contract("_ -> param1")
    public static <T> T uncheckedCast(@Nullable Object o) {
        //noinspection unchecked
        return (T) o;
    }
}
