package me.supcheg.advancedmanhunt.gui.impl.common;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.CustomLog;
import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.context.GuiTickContext;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import me.supcheg.advancedmanhunt.gui.impl.common.logic.LogicDelegate;
import me.supcheg.advancedmanhunt.gui.impl.common.logic.LogicDelegatingAdvancedGui;
import me.supcheg.advancedmanhunt.util.OtherCollections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@CustomLog
public abstract class Gui implements LogicDelegatingAdvancedGui {
    protected final String key;
    protected final int rows;
    protected final ResourceController<String> backgroundController;
    protected final Map<At, List<GuiTicker>> tickConsumers;
    protected final LogicDelegate logicDelegate;
    protected final GuiTickContext context;

    protected Gui(@NotNull AdvancedGuiBuilder builder, @NotNull LogicDelegate logicDelegate) {
        this.key = builder.getKey();
        this.rows = builder.getRows();
        this.backgroundController = new ResourceController<>(builder.getBackground());
        this.tickConsumers = GuiCollections.buildSortedConsumersMap(builder.getTickers());
        this.logicDelegate = logicDelegate;
        this.context = new GuiTickContext(this);
    }

    protected void acceptAllConsumersWithAt(@NotNull At at, @NotNull GuiTickContext ctx) {
        for (GuiTicker ticker : tickConsumers.get(at)) {
            try {
                ticker.getConsumer().accept(ctx);
            } catch (Exception e) {
                log.error("An error occurred while accepting tick consumer", e);
            }
        }
    }

    @Override
    public void setBackground(@NotNull String path) {
        Objects.requireNonNull(path, "path");
        backgroundController.setResource(path);
    }

    @NotNull
    @Override
    public String getBackground() {
        return backgroundController.getResource();
    }

    @NotNull
    @Override
    public AdvancedGuiBuilder toBuilder() {
        AdvancedGuiBuilder builder = AdvancedGuiBuilder.gui()
                .key(key)
                .rows(rows)
                .background(backgroundController.getInitialResource());

        buttonsToBuilders().forEach(builder::button);

        tickConsumers.values().forEach(builder.getTickers()::addAll);

        return builder;
    }

    @NotNull
    private List<AdvancedButtonBuilder> buttonsToBuilders() {
        Map<AdvancedButtonBuilder, IntList> compact = new HashMap<>();

        AdvancedButton[] slot2button = getButtons();

        for (int slot = 0; slot < slot2button.length; slot++) {
            AdvancedButton button = slot2button[slot];
            if (button != null) {
                compact.computeIfAbsent(button.toBuilderWithoutSlots(), __ -> new IntArrayList(1))
                        .add(slot);
            }
        }

        List<AdvancedButtonBuilder> buttons = new ArrayList<>(compact.size());
        compact.forEach((builder, slots) -> {
            builder.getSlots().addAll(slots);
            buttons.add(builder);
        });

        buttons.sort(Comparator.comparing(builder -> builder.getSlots().size()));

        return buttons;
    }

    @Nullable
    protected abstract AdvancedButton @NotNull [] getButtons();

    @NotNull
    @Override
    public Collection<GuiTicker> getTickers() {
        return OtherCollections.concat(tickConsumers.values());
    }
}
