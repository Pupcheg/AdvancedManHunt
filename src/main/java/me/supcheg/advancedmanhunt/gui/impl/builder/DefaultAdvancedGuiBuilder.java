package me.supcheg.advancedmanhunt.gui.impl.builder;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.controller.DefaultAdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.impl.controller.inventory.IndividualGuiInventoryController;
import me.supcheg.advancedmanhunt.gui.impl.controller.inventory.SharedGuiInventoryController;
import me.supcheg.advancedmanhunt.gui.impl.controller.resource.GuiResourceController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
public class DefaultAdvancedGuiBuilder implements AdvancedGuiBuilder {

    private static final GuiBackgroundFunction DEFAULT_BACKGROUND = GuiBackgroundFunction.constant("");

    private final DefaultAdvancedGuiController controller;

    private int rows = 3;
    private boolean individual = false;
    private final List<DefaultAdvancedButtonBuilder> buttons = new ArrayList<>();
    private GuiBackgroundFunction background = DEFAULT_BACKGROUND;
    private Duration backgroundChangePeriod = Duration.INFINITY;

    @Override
    public AdvancedGuiBuilder rows(int rows) {
        if (rows < 0 || rows > 6) {
            throw new IllegalArgumentException();
        }

        this.rows = rows;
        return this;
    }

    @Override
    public AdvancedGuiBuilder individual() {
        individual = true;
        return this;
    }

    @Override
    public AdvancedGuiBuilder button(AdvancedButtonBuilder button) {
        Objects.requireNonNull(button, "button");

        if (!(button instanceof DefaultAdvancedButtonBuilder defaultButtonBuilder)) {
            throw new IllegalArgumentException();
        }
        buttons.add(defaultButtonBuilder);
        return this;
    }

    @Override
    public AdvancedGuiBuilder background(String pngSubPath) {
        Objects.requireNonNull(pngSubPath, "pngSubPath");
        background = GuiBackgroundFunction.constant(pngSubPath);
        return this;
    }

    @Override
    public AdvancedGuiBuilder animatedBackground(String pngSubPathTemplate, int size, Duration period) {
        Objects.requireNonNull(pngSubPathTemplate, "pngSubPathTemplate");
        Objects.requireNonNull(period, "period");
        background = GuiBackgroundFunction.sizedAnimation(pngSubPathTemplate, size);
        backgroundChangePeriod = period;
        return this;
    }

    @Override
    public AdvancedGuiBuilder lazyBackground(GuiBackgroundFunction function) {
        Objects.requireNonNull(function, "function");
        background = function;
        return this;
    }

    @Override
    public AdvancedGuiBuilder lazyAnimatedBackground(GuiBackgroundFunction function, Duration period) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");
        background = function;
        backgroundChangePeriod = period;
        return this;
    }

    public DefaultAdvancedGui build() {
        AdvancedGuiHolder guiHolder = new AdvancedGuiHolder();

        DefaultAdvancedGui gui = new DefaultAdvancedGui(
                rows,
                individual ? new IndividualGuiInventoryController(rows, guiHolder) : new SharedGuiInventoryController(rows, guiHolder),
                new GuiResourceController<>(background, backgroundChangePeriod)
        );
        buttons.forEach(gui::addButton);

        guiHolder.setGui(gui);
        return gui;
    }

    @Override
    public AdvancedGui buildAndRegister() {
        return controller.buildAndRegister(this);
    }
}
