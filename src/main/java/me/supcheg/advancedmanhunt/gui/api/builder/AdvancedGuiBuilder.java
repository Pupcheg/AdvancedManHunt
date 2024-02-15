package me.supcheg.advancedmanhunt.gui.api.builder;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.gui.api.functional.AdvancedButtonConfigurer;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@CanIgnoreReturnValue
public sealed interface AdvancedGuiBuilder permits AdvancedGuiBuilderImpl {
    int DEFAULT_ROWS = 3;
    String DEFAULT_BACKGROUND = "gui/no_texture_gui.png";
    AdvancedButtonConfigurer DEFAULT_BUTTON_CONFIGURER = Function.identity()::apply;

    @NotNull
    @Contract("-> new")
    static AdvancedGuiBuilder builder() {
        return new AdvancedGuiBuilderImpl();
    }

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
    @Contract("_ -> this")
    default AdvancedGuiBuilder ticker(@NotNull GuiTicker.Builder ticker) {
        Objects.requireNonNull(ticker, "ticker");
        ticker(ticker.build());
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder ticker(@NotNull GuiTicker ticker);


    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder background(@NotNull String path);

    @NotNull
    @Contract("_ -> this")
    AdvancedGuiBuilder buttonConfigurer(@NotNull AdvancedButtonConfigurer buttonConfigurer);


    @NotNull
    String getKey();

    int getRows();

    @NotNull
    List<AdvancedButtonBuilder> getButtons();

    @NotNull
    List<GuiTicker> getTickers();

    @NotNull
    String getBackground();

    @NotNull
    AdvancedButtonConfigurer getButtonConfigurer();
}
