package me.supcheg.advancedmanhunt.gui.impl.builder;

import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.controller.DefaultAdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import me.supcheg.advancedmanhunt.gui.impl.type.DefaultAdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.type.SingletonAdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.wrapped.WrappedGuiTickConsumer;
import me.supcheg.advancedmanhunt.packet.TitleSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DefaultAdvancedGuiBuilder implements AdvancedGuiBuilder {

    private static final int DEFAULT_ROWS = 3;
    private static final GuiBackgroundFunction DEFAULT_BACKGROUND = GuiBackgroundFunction.constant("gui/no_texture_gui.png");
    private static final Duration DEFAULT_CHANGE_PERIOD = Duration.INFINITY;

    private final DefaultAdvancedGuiController controller;
    private final TextureWrapper textureWrapper;
    private final TitleSender titleSender;

    private String key;
    private int rows;
    private final List<DefaultAdvancedButtonBuilder> buttons;
    private final List<WrappedGuiTickConsumer> tickConsumers;
    private GuiBackgroundFunction background;
    private Duration backgroundChangePeriod;

    public DefaultAdvancedGuiBuilder(@NotNull DefaultAdvancedGuiController controller,
                                     @NotNull TextureWrapper textureWrapper,
                                     @NotNull TitleSender titleSender) {
        this.controller = controller;
        this.textureWrapper = textureWrapper;
        this.titleSender = titleSender;

        this.rows = DEFAULT_ROWS;

        this.buttons = new ArrayList<>();
        this.tickConsumers = new ArrayList<>();

        this.background = DEFAULT_BACKGROUND;
        this.backgroundChangePeriod = DEFAULT_CHANGE_PERIOD;
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
    @Override
    public AdvancedGuiBuilder tick(@NotNull At at, @NotNull Priority priority, @NotNull GuiTickConsumer consumer) {
        this.tickConsumers.add(new WrappedGuiTickConsumer(at, priority, consumer));
        return this;
    }

    @NotNull
    @Contract("-> new")
    public DefaultAdvancedGui build() {
        sortAndTrim(tickConsumers);

        AdvancedGuiHolder holder = new AdvancedGuiHolder();
        SingletonAdvancedGui gui = new SingletonAdvancedGui(
                key,
                controller,
                rows,
                textureWrapper,
                titleSender,
                holder,
                new ResourceController<>(background, backgroundChangePeriod),
                tickConsumers
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

    private <T extends Comparable<T>> void sortAndTrim(@NotNull List<T> list) {
        list.sort(Comparator.naturalOrder());
        if (list instanceof ArrayList<T> arrayList) {
            arrayList.trimToSize();
        }
    }
}
