package me.supcheg.advancedmanhunt.gui.api.builder;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@CanIgnoreReturnValue
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
    @Contract("_ -> this")
    default AdvancedGuiBuilder ticker(@NotNull GuiTicker.Builder ticker) {
        Objects.requireNonNull(ticker);
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
    @Contract("-> new")
    AdvancedGui buildAndRegister();


    @NotNull
    String getKey();

    int getRows();

    @NotNull
    List<AdvancedButtonBuilder> getButtons();

    @NotNull
    List<GuiTicker> getTickers();

    @NotNull
    String getBackground();
}
