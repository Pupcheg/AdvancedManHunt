package me.supcheg.advancedmanhunt.gui.impl;

import lombok.CustomLog;
import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultButtonTemplate;
import me.supcheg.advancedmanhunt.gui.impl.controller.DefaultAdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@CustomLog
public class DefaultAdvancedGui implements AdvancedGui {
    private final String key;
    private final DefaultAdvancedGuiController controller;
    private final int rows;
    private final TextureWrapper textureWrapper;
    private final TitleSender titleSender;
    private final Inventory inventory;
    private final ResourceController<GuiBackgroundFunction, GuiResourceGetContext, String> backgroundController;
    private final DefaultAdvancedButton[] slot2button;
    private final Map<At, List<GuiTicker>> tickConsumers;
    private final GuiResourceGetContext context;

    public DefaultAdvancedGui(@NotNull String key,
                              @NotNull DefaultAdvancedGuiController controller,
                              int rows,
                              @NotNull TextureWrapper textureWrapper,
                              @NotNull TitleSender titleSender,
                              @NotNull AdvancedGuiHolder guiHolder,
                              @NotNull ResourceController<GuiBackgroundFunction, GuiResourceGetContext, String> backgroundController,
                              @NotNull List<GuiTicker> tickers) {
        this.key = key;
        this.controller = controller;
        int size = rows * 9;
        this.rows = rows;
        this.textureWrapper = textureWrapper;
        this.titleSender = titleSender;
        this.inventory = Bukkit.createInventory(guiHolder, size, Component.empty());
        this.backgroundController = backgroundController;
        this.slot2button = new DefaultAdvancedButton[size];
        this.tickConsumers = GuiCollections.buildSortedConsumersMap(tickers);
        this.context = new GuiResourceGetContext(this);
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
            DefaultAdvancedButton button = slot2button[slot];

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

    private void acceptAllConsumersWithAt(@NotNull At at, @NotNull GuiResourceGetContext ctx) {
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

        DefaultAdvancedButton button = slot2button[clickedSlot];

        if (button != null) {
            button.handleClick(event);
        }
    }

    public void addButton(@NotNull DefaultAdvancedButtonBuilder builder) {
        DefaultButtonTemplate template = builder.asTemplate();
        for (int slot : template.getSlots()) {
            if (slot2button[slot] != null) {
                throw new IllegalStateException("Already has a button at " + slot);
            }
            slot2button[slot] = template.createButton(this);
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
    public void setBackground(@NotNull GuiBackgroundFunction function) {
        Objects.requireNonNull(function, "function");
        backgroundController.setFunction(function);
    }
}
