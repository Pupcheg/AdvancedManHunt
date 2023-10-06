package me.supcheg.advancedmanhunt.gui.impl.type;

import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedButton;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import me.supcheg.advancedmanhunt.gui.impl.wrapped.WrappedGuiTickConsumer;
import me.supcheg.advancedmanhunt.packet.TitleSender;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@Getter
public class SingletonAdvancedGui implements DefaultAdvancedGui {
    private final int rows;
    private final TextureWrapper textureWrapper;
    private final TitleSender titleSender;
    private final Inventory inventory;
    private final ResourceController<GuiBackgroundFunction, GuiResourceGetContext, String> backgroundController;
    private final DefaultAdvancedButton[] slot2button;
    private final List<WrappedGuiTickConsumer> tickConsumers;

    public SingletonAdvancedGui(int rows,
                                @NotNull TextureWrapper textureWrapper,
                                @NotNull TitleSender titleSender,
                                @NotNull AdvancedGuiHolder guiHolder,
                                @NotNull ResourceController<GuiBackgroundFunction, GuiResourceGetContext, String> backgroundController,
                                @NotNull List<WrappedGuiTickConsumer> tickConsumers) {
        int size = rows * 9;
        this.rows = rows;
        this.textureWrapper = textureWrapper;
        this.titleSender = titleSender;
        this.inventory = Bukkit.createInventory(guiHolder, size, Component.empty());
        this.backgroundController = backgroundController;
        this.slot2button = new DefaultAdvancedButton[size];
        this.tickConsumers = tickConsumers;
    }

    @Override
    public void tick() {
        tickWithPlayer(null);
    }

    public void tickWithPlayer(@Nullable Player player) {
        GuiResourceGetContext ctx = new GuiResourceGetContext(this, player);

        acceptAllConsumersWithAt(At.TICK_START, ctx);

        backgroundController.tick(ctx);

        if (backgroundController.isUpdated()) {
            String key = backgroundController.getResource();
            Component title = textureWrapper.getGuiBackgroundComponent(key);

            if (player == null) {
                for (HumanEntity viewer : inventory.getViewers()) {
                    titleSender.sendTitle(viewer.getOpenInventory(), title);
                }
            } else {
                titleSender.sendTitle(player.getOpenInventory(), title);
            }
        }

        for (int slot = 0; slot < slot2button.length; slot++) {
            DefaultAdvancedButton button = slot2button[slot];

            if (button == null) {
                continue;
            }

            button.tick(slot, player);

            if (button.isUpdated()) {
                inventory.setItem(slot, button.render());
            }
        }

        acceptAllConsumersWithAt(At.TICK_END, ctx);
    }

    private void acceptAllConsumersWithAt(@NotNull At at, @NotNull GuiResourceGetContext ctx) {
        for (WrappedGuiTickConsumer tickConsumer : tickConsumers) {
            if (tickConsumer.getAt() == at) {
                tickConsumer.accept(ctx);
            }
        }
    }

    @Override
    public void handleClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        int clickedSlot = event.getSlot();

        if (clickedSlot < 0 || clickedSlot >= rows * 9) {
            return;
        }

        DefaultAdvancedButton button = slot2button[clickedSlot];

        if (button != null) {
            button.handleClick((Player) event.getWhoClicked(), clickedSlot);
        }
    }

    @Override
    public void handleClose(@NotNull InventoryCloseEvent event) {
    }

    public void addButton(@NotNull DefaultAdvancedButtonBuilder builder) {
        for (int slot : builder.getSlots()) {
            if (slot2button[slot] != null) {
                throw new IllegalStateException("Already has a button at " + slot);
            }
            slot2button[slot] = builder.build(this);
        }
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public boolean isIndividual() {
        return false;
    }

    @Nullable
    @Override
    public InventoryView open(@NotNull Player player) {
        return player.openInventory(inventory);
    }

    @Override
    public void setBackground(@NotNull GuiBackgroundFunction function) {
        Objects.requireNonNull(function, "function");

        backgroundController.setFunction(function);
    }

    @Override
    public void setAnimatedBackground(@NotNull GuiBackgroundFunction function, @NotNull Duration period) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");

        backgroundController.setFunctionWithChangePeriod(function, period);
    }
}
