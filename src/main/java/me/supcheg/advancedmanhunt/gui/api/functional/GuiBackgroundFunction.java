package me.supcheg.advancedmanhunt.gui.api.functional;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
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

    class SizedAnimationGuiBackgroundFunction implements GuiBackgroundFunction {
        private static final Pattern PATTERN = Pattern.compile("<n>", Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
        private final String pngSubPathTemplate;
        private final int size;
        private final Object2IntMap<UUID> player2lastIndex;

        public SizedAnimationGuiBackgroundFunction(String pngSubPathTemplate, int size) {
            this.pngSubPathTemplate = pngSubPathTemplate;
            this.size = size;
            this.player2lastIndex = new Object2IntOpenHashMap<>();
            player2lastIndex.defaultReturnValue(-1);
        }

        @NotNull
        @Override
        public String getBackground(@NotNull GuiResourceGetContext ctx) {
            UUID key = ctx.hasPlayer() ? ctx.getPlayer().getUniqueId() : null;
            int lastIndex = player2lastIndex.getInt(key);

            if (++lastIndex > size) {
                lastIndex = 0;
            }
            player2lastIndex.put(key, lastIndex);

            return PATTERN.matcher(pngSubPathTemplate).replaceFirst(String.valueOf(player2lastIndex));
        }
    }
}
