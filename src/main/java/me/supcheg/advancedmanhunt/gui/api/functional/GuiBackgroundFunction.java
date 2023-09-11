package me.supcheg.advancedmanhunt.gui.api.functional;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

@FunctionalInterface
public interface GuiBackgroundFunction extends Function<GuiResourceGetContext, String> {
    String getBackground(GuiResourceGetContext ctx);

    @Override
    default String apply(GuiResourceGetContext ctx) {
        return getBackground(ctx);
    }

    static GuiBackgroundFunction constant(String path) {
        return ctx -> path;
    }

    static GuiBackgroundFunction sizedAnimation(String pngSubPathTemplate, int size) {
        Objects.requireNonNull(pngSubPathTemplate, "pngSubPathTemplate");
        return new SizedAnimationGuiBackgroundFunction(pngSubPathTemplate, size);
    }

    @RequiredArgsConstructor
    class SizedAnimationGuiBackgroundFunction implements GuiBackgroundFunction {
        private static final Pattern PATTERN = Pattern.compile("<n>", Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
        private final String pngSubPathTemplate;
        private final int size;
        private int lastIndex = -1;

        @Override
        public String getBackground(GuiResourceGetContext ctx) {
            if (++lastIndex > size) {
                lastIndex = 0;
            }
            return PATTERN.matcher(pngSubPathTemplate).replaceFirst(String.valueOf(lastIndex));
        }
    }
}
