package me.supcheg.advancedmanhunt.gui.impl.type;

import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedButton;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.impl.controller.resource.GuiResourceController;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class SingletonAdvancedGui implements DefaultAdvancedGui {
    private final int rows;
    private final Inventory inventory;
    private final GuiResourceController<GuiBackgroundFunction, String> backgroundController;
    private final DefaultAdvancedButton[] slot2button;

    public SingletonAdvancedGui(int rows,
                                @NotNull AdvancedGuiHolder guiHolder,
                                @NotNull GuiResourceController<GuiBackgroundFunction, String> backgroundController) {
        int size = rows * 9;
        this.rows = rows;
        this.inventory = Bukkit.createInventory(guiHolder, size, Component.empty());
        this.backgroundController = backgroundController;
        this.slot2button = new DefaultAdvancedButton[size];
    }

    @Override
    public void tick() {
        tickWithPlayer(null);
    }

    public void tickWithPlayer(@Nullable Player player) {
        backgroundController.tick(this, player);

        for (int slot = 0; slot < slot2button.length; slot++) {
            DefaultAdvancedButton button = slot2button[slot];

            button.tick(slot, player);

            if (button.isUpdated()) {
                inventory.setItem(slot, button.render());
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
    public void setBackground(@NotNull String pngSubPath) {
        Objects.requireNonNull(pngSubPath, "pngSubPath");

        backgroundController.setFunction(GuiBackgroundFunction.constant(pngSubPath));
    }

    @Override
    public void animatedBackground(@NotNull String pngSubPathTemplate, int size, @NotNull Duration period) {
        Objects.requireNonNull(pngSubPathTemplate, "pngSubPathTemplate");
        Objects.requireNonNull(period, "period");

        GuiBackgroundFunction function = GuiBackgroundFunction.sizedAnimation(pngSubPathTemplate, size);
        backgroundController.setFunctionWithChangePeriod(function, period.getTicks());
    }

    @Override
    public void lazyBackground(@NotNull GuiBackgroundFunction function) {
        Objects.requireNonNull(function, "function");

        backgroundController.setFunction(function);
    }

    @Override
    public void lazyAnimatedBackground(@NotNull GuiBackgroundFunction function, @NotNull Duration period) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");

        backgroundController.setFunctionWithChangePeriod(function, period.getTicks());
    }
}
