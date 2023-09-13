package me.supcheg.advancedmanhunt.gui.api.functional;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

@FunctionalInterface
public interface GuiBackgroundFunction extends Function<GuiResourceGetContext, String> {
    @NotNull
    String getBackground(@NotNull GuiResourceGetContext ctx);

    @NotNull
    @Override
    default String apply(@NotNull GuiResourceGetContext ctx) {
        return getBackground(ctx);
    }

    @NotNull
    @Contract("_ -> new")
    static GuiBackgroundFunction constant(@NotNull String path) {
        return ctx -> path;
    }

    @NotNull
    @Contract("_, _ -> new")
    static GuiBackgroundFunction sizedAnimation(@NotNull String pngSubPathTemplate, int size) {
        Objects.requireNonNull(pngSubPathTemplate, "pngSubPathTemplate");
        return new SizedAnimationGuiBackgroundFunction(pngSubPathTemplate, size);
    }

    @RequiredArgsConstructor
    class SizedAnimationGuiBackgroundFunction implements GuiBackgroundFunction {
        private static final Pattern PATTERN = Pattern.compile("<n>", Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
        private final String pngSubPathTemplate;
        private final int size;
        private int lastIndex = -1;

        @NotNull
        @Override
        public String getBackground(@NotNull GuiResourceGetContext ctx) {
            if (++lastIndex > size) {
                lastIndex = 0;
            }
            return PATTERN.matcher(pngSubPathTemplate).replaceFirst(String.valueOf(lastIndex));
        }
    }
}
