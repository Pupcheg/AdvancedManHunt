package me.supcheg.advancedmanhunt.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Keys {
    @SuppressWarnings("PatternValidation")
    @NotNull
    @Contract("_ -> new")
    public static Key key(@NotNull String key) {
        return Key.key(key);
    }
}
