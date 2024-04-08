package me.supcheg.advancedmanhunt.gui.api.key;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @see DefaultKeyModifier
 */
public interface KeyModifier {
    @NotNull
    Key modify(@NotNull Key key, @NotNull Collection<Key> knownKeys);
}
