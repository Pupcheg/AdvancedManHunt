package me.supcheg.advancedmanhunt.gui.api.builder;

import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface AdvancedGuiBuilder {

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder rows(int rows);

    @NotNull
    @Contract("-> this")
    AdvancedGuiBuilder individual();

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder button(@NotNull AdvancedButtonBuilder button);

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder background(@NotNull String pngSubPath);

    @NotNull
    @Contract("_, _, _ -> this")
    AdvancedGuiBuilder animatedBackground(@NotNull String pngSubPathTemplate, int size, @NotNull Duration period);

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder lazyBackground(@NotNull GuiBackgroundFunction function);

    @NotNull
    @Contract("_, _ -> this")
    AdvancedGuiBuilder lazyAnimatedBackground(@NotNull GuiBackgroundFunction function, Duration period);

    @NotNull
    @Contract("-> new")
    AdvancedGui buildAndRegister();
}
