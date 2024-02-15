package me.supcheg.advancedmanhunt.gui.api.builder;

import me.supcheg.advancedmanhunt.gui.api.functional.AdvancedButtonConfigurer;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class AdvancedGuiBuilderImpl implements AdvancedGuiBuilder {
    private String key;
    private int rows;
    private final List<AdvancedButtonBuilder> buttons;
    private final List<GuiTicker> tickers;
    private String background;
    private AdvancedButtonConfigurer buttonConfigurer;

    AdvancedGuiBuilderImpl() {
        this.rows = DEFAULT_ROWS;
        this.buttons = new ArrayList<>();
        this.tickers = new ArrayList<>();
        this.background = DEFAULT_BACKGROUND;
        this.buttonConfigurer = DEFAULT_BUTTON_CONFIGURER;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedGuiBuilder key(@NotNull String key) {
        Objects.requireNonNull(key, "key");
        this.key = key;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedGuiBuilder rows(int rows) {
        if (rows < 1 || rows > 6) {
                throw new IllegalArgumentException("Rows count shouldn't be lower than 1 and upper than 6");
        }

        this.rows = rows;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedGuiBuilder button(@NotNull AdvancedButtonBuilder button) {
        Objects.requireNonNull(button, "button");
        buttons.add(button);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedGuiBuilder background(@NotNull String path) {
        Objects.requireNonNull(path, "path");
        background = path;
        return this;
    }

    @NotNull
    @Override
    public AdvancedGuiBuilder ticker(@NotNull GuiTicker ticker) {
        Objects.requireNonNull(ticker, "ticker");
        this.tickers.add(ticker);
        return this;
    }

    @NotNull
    @Override
    public AdvancedGuiBuilder buttonConfigurer(@NotNull AdvancedButtonConfigurer buttonConfigurer) {
        Objects.requireNonNull(buttonConfigurer, "buttonConfigurer");
        this.buttonConfigurer = buttonConfigurer;
        return this;
    }

    @NotNull
    @Override
    public String getKey() {
        return Objects.requireNonNull(key, "'key' is not set");
    }

    @Override
    public int getRows() {
        return rows;
    }

    @NotNull
    @Override
    public List<AdvancedButtonBuilder> getButtons() {
        return buttons;
    }

    @NotNull
    @Override
    public List<GuiTicker> getTickers() {
        return tickers;
    }

    @NotNull
    @Override
    public String getBackground() {
        return background;
    }

    @NotNull
    @Override
    public AdvancedButtonConfigurer getButtonConfigurer() {
        return buttonConfigurer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AdvancedGuiBuilderImpl that)) {
            return false;
        }

        return rows == that.rows
                && Objects.equals(key, that.key)
                && buttons.equals(that.buttons)
                && tickers.equals(that.tickers)
                && background.equals(that.background)
                && buttonConfigurer.equals(that.buttonConfigurer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                key,
                rows,
                buttons,
                tickers,
                background,
                buttonConfigurer
        );
    }
}
