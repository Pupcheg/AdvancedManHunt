package me.supcheg.advancedmanhunt.gui.impl.inventory;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.CustomLog;
import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.context.GuiTickContext;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import me.supcheg.advancedmanhunt.gui.impl.common.GuiCollections;
import me.supcheg.advancedmanhunt.gui.impl.common.ResourceController;
import me.supcheg.advancedmanhunt.gui.impl.inventory.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.json.LogicDelegatingAdvancedGui;
import me.supcheg.advancedmanhunt.util.TitleSender;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@CustomLog
public class InventoryGui implements LogicDelegatingAdvancedGui {
    private final String key;
    private final InventoryGuiController controller;
    private final int rows;
    private final TextureWrapper textureWrapper;
    private final TitleSender titleSender;
    private final Inventory inventory;
    private final ResourceController<String> backgroundController;
    private final InventoryButton[] slot2button;
    private final Map<At, List<GuiTicker>> tickConsumers;
    private final GuiTickContext context;
    private final Object logicInstance;

    public InventoryGui(@NotNull InventoryGuiController controller,
                        @NotNull TextureWrapper textureWrapper,
                        @NotNull TitleSender titleSender,
                        @NotNull InventoryGuiHolder guiHolder,
                        @NotNull AdvancedGuiBuilder builder,
                        @Nullable Object logicInstance) {
        this.key = builder.getKey();
        this.controller = controller;
        this.rows = builder.getRows();
        this.textureWrapper = textureWrapper;
        this.titleSender = titleSender;
        this.inventory = Bukkit.createInventory(guiHolder, rows * 9, Component.empty());
        this.backgroundController = new ResourceController<>(builder.getBackground());
        this.slot2button = new InventoryButton[rows * 9];
        this.tickConsumers = GuiCollections.buildSortedConsumersMap(builder.getTickers());
        this.context = new GuiTickContext(this);
        this.logicInstance = logicInstance;
    }

    public void tick() {
        acceptAllConsumersWithAt(At.TICK_START, context);

        if (backgroundController.pollUpdated()) {
            String key = backgroundController.getResource();
            Component title = textureWrapper.getGuiBackgroundComponent(key);

            for (HumanEntity viewer : inventory.getViewers()) {
                titleSender.sendTitle(viewer.getOpenInventory(), title);
            }
        }

        for (int slot = 0; slot < slot2button.length; slot++) {
            InventoryButton button = slot2button[slot];

            if (button == null) {
                continue;
            }

            button.tick(slot);

            if (button.pollUpdated()) {
                button.render().setAt(inventory, slot);
            }
        }

        acceptAllConsumersWithAt(At.TICK_END, context);
    }

    private void acceptAllConsumersWithAt(@NotNull At at, @NotNull GuiTickContext ctx) {
        for (GuiTicker ticker : tickConsumers.get(at)) {
            try {
                ticker.getConsumer().accept(ctx);
            } catch (Exception e) {
                log.error("An error occurred while accepting tick consumer", e);
            }
        }
    }

    public void handleClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        int clickedSlot = event.getSlot();

        if (clickedSlot < 0 || clickedSlot >= rows * 9) {
            return;
        }

        ClickType clickType = event.getClick();
        if (clickType == ClickType.DOUBLE_CLICK) {
            return;
        }

        InventoryButton button = slot2button[clickedSlot];

        if (button != null) {
            button.handleClick(event);
        }
    }

    public void addButton(@NotNull AdvancedButtonBuilder builder) {
        if (builder.getSlots().isEmpty()) {
            throw new IllegalArgumentException("The button has no slots");
        }

        for (int slot : builder.getSlots()) {
            if (slot2button[slot] != null) {
                throw new IllegalStateException("Already has a button at " + slot);
            }
            slot2button[slot] = new InventoryButton(this, controller.getButtonRenderer(), builder);
        }
    }

    @Nullable
    @Override
    public InventoryView open(@NotNull Player player) {
        InventoryView view = player.openInventory(inventory);
        if (view != null) {
            Component title = textureWrapper.getGuiBackgroundComponent(backgroundController.getResource());
            titleSender.sendTitle(view, title);
        }
        return view;
    }

    @Override
    public void setBackground(@NotNull String path) {
        Objects.requireNonNull(path, "path");
        backgroundController.setResource(path);
    }

    @NotNull
    @Override
    public AdvancedGuiBuilder toBuilder() {
        AdvancedGuiBuilder builder = AdvancedGuiBuilder.gui()
                .key(key)
                .rows(rows)
                .background(backgroundController.getInitialResource());

        buttonsToBuilders().forEach(builder::button);

        for (List<GuiTicker> values : tickConsumers.values()) {
            builder.getTickers().addAll(values);
        }

        return builder;
    }

    @NotNull
    private List<AdvancedButtonBuilder> buttonsToBuilders() {
        Map<AdvancedButtonBuilder, IntList> compact = new HashMap<>();

        for (int slot = 0; slot < slot2button.length; slot++) {
            InventoryButton button = slot2button[slot];
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

    @Override
    public boolean hasLogicInstance() {
        return logicInstance != null;
    }

    @NotNull
    public Object getLogicInstance() {
        return Objects.requireNonNull(logicInstance, "This gui doesn't have a logic instance");
    }
}
