package me.supcheg.advancedmanhunt.gui.api.builder;

import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public interface AdvancedGuiBuilder {

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder key(@NotNull String key);

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder rows(int rows);

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder button(@NotNull AdvancedButtonBuilder button);

    @NotNull
    @Contract("_, _ -> this")
    default AdvancedGuiBuilder generateButtons(int @NotNull [] ints, @NotNull IntFunction<AdvancedButtonBuilder> function) {
        return generateButtons(IntStream.of(ints), function);
    }

    @NotNull
    @Contract("_, _ -> this")
    default AdvancedGuiBuilder generateButtons(@NotNull IntStream range, @NotNull IntFunction<AdvancedButtonBuilder> function) {
        range.mapToObj(function).forEach(this::button);
        return this;
    }


    @NotNull
    @Contract("_, _ -> this")
    default AdvancedGuiBuilder tick(@NotNull At at, @NotNull GuiTickConsumer consumer) {
        return tick(at, Priority.NORMAL, consumer);
    }

    @NotNull
    @Contract("_, _, _ -> this")
    AdvancedGuiBuilder tick(@NotNull At at, @NotNull Priority priority, @NotNull GuiTickConsumer consumer);


    @NotNull
    @Contract("_ -> this")
    default AdvancedGuiBuilder background(@NotNull String pngSubPath) {
        Objects.requireNonNull(pngSubPath, "pngSubPath");
        return background(GuiBackgroundFunction.constant(pngSubPath));
    }

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder background(@NotNull GuiBackgroundFunction function);

    @NotNull
    @Contract("_, _ -> this")
    AdvancedGuiBuilder animatedBackground(@NotNull GuiBackgroundFunction function, Duration period);

    @NotNull
    @Contract("-> new")
    AdvancedGui buildAndRegister();
}
