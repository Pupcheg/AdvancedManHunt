package me.supcheg.advancedmanhunt.paper;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ComponentFormatter {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("{}", Pattern.LITERAL);
    private static final Component SERIALIZED_NULL = Component.text("null");

    @NotNull
    public static Component format(@NotNull Component text, @Nullable Object @NotNull ... replacements) {
        return switch (replacements.length) {
            case 0 -> Objects.requireNonNull(text);
            case 1 -> format(text, replacements[0]);
            default -> text.replaceText(
                    TextReplacementConfig.builder()
                            .match(DEFAULT_PATTERN)
                            .replacement(new SequentialReplacer(replacements))
                            .build()
            );
        };
    }

    @NotNull
    public static Component format(@NotNull Component text, @Nullable Object replacement) {
        return text.replaceText(
                TextReplacementConfig.builder()
                        .match(DEFAULT_PATTERN)
                        .once()
                        .replacement(String.valueOf(replacement))
                        .build()
        );
    }

    @NotNull
    @Contract("_ -> param1")
    @Deprecated
    public static Component format(@NotNull Component text) {
        return Objects.requireNonNull(text);
    }

    @RequiredArgsConstructor
    private static final class SequentialReplacer implements Function<TextComponent.Builder, ComponentLike> {

        private final Object[] replacements;
        private int lastIndex = -1;

        @NotNull
        @Override
        public ComponentLike apply(@NotNull TextComponent.Builder builder) {
            Object currentReplacement = replacements[++lastIndex];

            if (currentReplacement == null) {
                return SERIALIZED_NULL;
            } else if (currentReplacement instanceof ComponentLike componentLike) {
                return componentLike;
            } else {
                return Component.text(currentReplacement.toString());
            }
        }
    }
}
