package me.supcheg.advancedmanhunt.gui.api.key;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @see DefaultKeyModifier
 */
public interface KeyModifier {
    @NotNull
    String modify(@NotNull String key, @NotNull Collection<String> knownKeys);
}
