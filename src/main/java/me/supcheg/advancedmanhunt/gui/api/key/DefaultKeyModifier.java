package me.supcheg.advancedmanhunt.gui.api.key;

import me.supcheg.advancedmanhunt.random.ThreadSafeRandom;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public enum DefaultKeyModifier implements KeyModifier {

    /**
     * {@code 'advancedmanhunt:gui' -> 'advancedmanhunt:gui'}
     * <p>
     * {@code 'gui' -> 'gui'}
     */
    NO_CHANGES {
        @NotNull
        @Override
        public String modify(@NotNull String key, @NotNull Collection<String> knownKeys) {
            return key;
        }
    },

    /**
     * {@code 'advancedmanhunt:gui' -> 'f0d3a5'}
     * <p>
     * {@code 'gui' -> 'f0d3a5'}
     */
    RANDOM_WITHOUT_NAMESPACE {
        @NotNull
        @Override
        public String modify(@NotNull String key, @NotNull Collection<String> knownKeys) {
            return ThreadSafeRandom.randomString(knownKeys::contains, 256);
        }
    },

    /**
     * {@code 'advancedmanhunt:gui'} -> {@code 'advancedmanhunt:f0d3a5'}
     * <p>
     * {@code 'gui'} -> {@code 'f0d3a5'}
     */
    RANDOM_WITH_NAMESPACE {
        @NotNull
        @Override
        public String modify(@NotNull String key, @NotNull Collection<String> knownKeys) {
            return getNamespaceOrEmpty(key) + ThreadSafeRandom.randomString(knownKeys::contains, 256);
        }

        @NotNull
        private String getNamespaceOrEmpty(@NotNull String key) {
            int separatorIndex = key.indexOf(':');
            return separatorIndex == -1 ? "" : key.substring(0, separatorIndex) + ':';
        }
    },

    /**
     * {@code 'advancedmanhunt:gui' -> 'advancedmanhunt:gui$f0d3a5'}
     * <p>
     * {@code 'gui' -> 'gui$f0d3a5'}
     */
    ADDITIONAL_HASH {
        @NotNull
        @Override
        public String modify(@NotNull String key, @NotNull Collection<String> knownKeys) {
            return key + '$' + ThreadSafeRandom.randomString(hash -> knownKeys.contains(key + '$' + hash), 256);
        }
    }
}
