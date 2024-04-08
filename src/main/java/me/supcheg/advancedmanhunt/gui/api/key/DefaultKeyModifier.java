package me.supcheg.advancedmanhunt.gui.api.key;

import me.supcheg.advancedmanhunt.random.ThreadSafeRandom;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static me.supcheg.advancedmanhunt.util.Keys.key;

public enum DefaultKeyModifier implements KeyModifier {

    /**
     * {@code 'advancedmanhunt:gui' -> 'advancedmanhunt:gui'}
     */
    NO_CHANGES {
        @NotNull
        @Override
        public Key modify(final @NotNull Key original, @NotNull Collection<Key> knownKeys) {
            return original;
        }
    },

    /**
     * {@code 'advancedmanhunt:gui'} -> {@code 'advancedmanhunt:f0d3a5'}
     */
    RANDOM {
        @NotNull
        @Override
        public Key modify(final @NotNull Key original, @NotNull Collection<Key> knownKeys) {
            Key candidate;
            do {
                candidate = key(original.namespace(), ThreadSafeRandom.randomString());
            } while (knownKeys.contains(candidate));
            return candidate;
        }
    },

    /**
     * {@code 'advancedmanhunt:gui' -> 'advancedmanhunt:gui$f0d3a5'}
     */
    ADDITIONAL_HASH {
        @NotNull
        @Override
        public Key modify(final @NotNull Key original, @NotNull Collection<Key> knownKeys) {
            Key candidate;
            do {
                candidate = key(original.namespace(), original.value() + '$' + ThreadSafeRandom.randomString());
            } while (knownKeys.contains(candidate));
            return candidate;
        }
    }
}
