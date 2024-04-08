package me.supcheg.advancedmanhunt.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PatternValidation")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Keys {
    @NotNull
    @Contract("_ -> new")
    public static Key key(@NotNull String string) {
        return Key.key(string);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Key key(@NotNull String namespace, @NotNull String value) {
        return Key.key(namespace, value);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static NamespacedKey asNamespaced(@Nullable Key key) {
        return key == null ? null :
                key instanceof NamespacedKey namespacedKey ? namespacedKey :
                        new NamespacedKey(key.namespace(), key.value());
    }

    @NotNull
    @Contract("_ -> new")
    public static Key advancedmanhuntKey(@NotNull String key) {
        return Key.key(AdvancedManHuntPlugin.NAMESPACE, key);
    }
}
