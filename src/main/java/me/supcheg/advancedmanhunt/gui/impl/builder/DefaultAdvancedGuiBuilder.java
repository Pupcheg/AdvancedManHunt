package me.supcheg.advancedmanhunt.gui.impl.builder;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.controller.DefaultAdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import me.supcheg.advancedmanhunt.gui.impl.type.DefaultAdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.type.IndividualAdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.type.SingletonAdvancedGui;
import me.supcheg.advancedmanhunt.packet.TitleSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
public class DefaultAdvancedGuiBuilder implements AdvancedGuiBuilder {

    private static final GuiBackgroundFunction DEFAULT_BACKGROUND = GuiBackgroundFunction.constant("gui/no_texture_gui.png");

    private final DefaultAdvancedGuiController controller;
    private final TextureWrapper textureWrapper;
    private final TitleSender titleSender;

    private int rows = 3;
    private boolean individual = false;
    private final List<DefaultAdvancedButtonBuilder> buttons = new ArrayList<>();
    private GuiBackgroundFunction background = DEFAULT_BACKGROUND;
    private Duration backgroundChangePeriod = Duration.INFINITY;

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
    @Contract("-> this")
    @Override
    public AdvancedGuiBuilder individual() {
        individual = true;
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
    public AdvancedGuiBuilder background(@NotNull GuiBackgroundFunction function) {
        Objects.requireNonNull(function, "function");
        background = function;
        backgroundChangePeriod = Duration.INFINITY;
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    @Override
    public AdvancedGuiBuilder animatedBackground(@NotNull GuiBackgroundFunction function, @NotNull Duration period) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");
        background = function;
        backgroundChangePeriod = period;
        return this;
    }

    @NotNull
    @Contract("-> new")
    public DefaultAdvancedGui buildCurrentType() {
        AdvancedGuiHolder holder = new AdvancedGuiHolder();

        DefaultAdvancedGui gui = individual ?
                new IndividualAdvancedGui(rows, this, holder) :
                buildSingleton(holder);

        holder.setGui(gui);

        return gui;
    }

    @NotNull
    @Contract("_ -> new")
    public SingletonAdvancedGui buildSingleton(@NotNull AdvancedGuiHolder guiHolder) {
        SingletonAdvancedGui gui = new SingletonAdvancedGui(
                rows,
                textureWrapper,
                titleSender,
                guiHolder,
                new ResourceController<>(background, backgroundChangePeriod)
        );
        buttons.forEach(gui::addButton);

        return gui;
    }

    @NotNull
    @Contract("-> new")
    @Override
    public DefaultAdvancedGui buildAndRegister() {
        DefaultAdvancedGui gui = buildCurrentType();
        controller.register(gui);
        return gui;
    }
}
