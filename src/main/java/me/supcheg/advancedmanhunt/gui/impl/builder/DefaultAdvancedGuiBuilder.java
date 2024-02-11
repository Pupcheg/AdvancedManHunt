package me.supcheg.advancedmanhunt.gui.impl.builder;

import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.controller.DefaultAdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import me.supcheg.advancedmanhunt.util.TitleSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class DefaultAdvancedGuiBuilder implements AdvancedGuiBuilder {

    private static final int DEFAULT_ROWS = 3;
    private static final String DEFAULT_BACKGROUND = "gui/no_texture_gui.png";

    private final DefaultAdvancedGuiController controller;
    private final TextureWrapper textureWrapper;
    private final TitleSender titleSender;

    private String key;
    private int rows;
    private final List<DefaultAdvancedButtonBuilder> buttons;
    private final List<GuiTicker> tickers;
    private String background;

    public DefaultAdvancedGuiBuilder(@NotNull DefaultAdvancedGuiController controller,
                                     @NotNull TextureWrapper textureWrapper,
                                     @NotNull TitleSender titleSender) {
        this.controller = controller;
        this.textureWrapper = textureWrapper;
        this.titleSender = titleSender;

        this.rows = DEFAULT_ROWS;

        this.buttons = new ArrayList<>();
        this.tickers = new ArrayList<>();

        this.background = DEFAULT_BACKGROUND;
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
        if (rows < 0 || rows > 6) {
            throw new IllegalArgumentException("Rows count shouldn't be lower than 0 and upper than 6");
        }

        this.rows = rows;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedGuiBuilder button(@NotNull AdvancedButtonBuilder button) {
        Objects.requireNonNull(button, "button");

        if (!(button instanceof DefaultAdvancedButtonBuilder defaultButtonBuilder)) {
            throw new IllegalArgumentException();
        }
        buttons.add(defaultButtonBuilder);
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
        Objects.requireNonNull(ticker);
        this.tickers.add(ticker);
        return this;
    }

    @NotNull
    @Contract("-> new")
    private DefaultAdvancedGui build() {
        AdvancedGuiHolder holder = new AdvancedGuiHolder();
        DefaultAdvancedGui gui = new DefaultAdvancedGui(
                key,
                controller,
                rows,
                textureWrapper,
                titleSender,
                holder,
                new ResourceController<>(background),
                tickers
        );
        buttons.forEach(gui::addButton);
        holder.setGui(gui);

        return gui;
    }

    @NotNull
    @Contract("-> new")
    @Override
    public DefaultAdvancedGui buildAndRegister() {
        DefaultAdvancedGui gui = build();
        controller.register(gui);
        return gui;
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
        return uncheckedCast(buttons);
    }

    @NotNull
    @Override
    public List<GuiTicker> getTickers() {
        return tickers;
    }

    @NotNull
    @Override
    public String getBackground() {
        return Objects.requireNonNull(background, "'background' is not set");
    }
}
