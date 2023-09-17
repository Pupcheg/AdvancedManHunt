package me.supcheg.advancedmanhunt.gui.impl.type;

import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedGuiBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class IndividualAdvancedGui implements DefaultAdvancedGui {
    private final int rows;
    private final DefaultAdvancedGuiBuilder builder;
    private final AdvancedGuiHolder guiHolder;
    private final Map<Player, SingletonAdvancedGui> player2gui;

    public IndividualAdvancedGui(int rows,
                                 @NotNull DefaultAdvancedGuiBuilder builder,
                                 @NotNull AdvancedGuiHolder guiHolder) {
        this.rows = rows;
        this.builder = builder;
        this.guiHolder = guiHolder;
        this.player2gui = new WeakHashMap<>();
    }

    @Override
    public void tick() {
        player2gui.forEach((player, gui) -> gui.tickWithPlayer(player));
    }

    @Override
    public void handleClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        SingletonAdvancedGui gui = player2gui.get(player);
        if (gui != null) {
            gui.handleClick(event);
        }
    }

    @Override
    public void handleClose(@NotNull InventoryCloseEvent event) {
        player2gui.remove((Player) event.getPlayer());
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public boolean isIndividual() {
        return true;
    }

    @Nullable
    @Override
    public InventoryView open(@NotNull Player player) {
        SingletonAdvancedGui gui = builder.buildSingleton(guiHolder);
        player2gui.put(player, gui);
        InventoryView inventoryView = gui.open(player);
        if (inventoryView == null) {
            player2gui.remove(player);
        }
        return inventoryView;
    }

    @Override
    public void setBackground(@NotNull String pngSubPath) {
        Objects.requireNonNull(pngSubPath, "pngSubPath");

        lazyBackground(GuiBackgroundFunction.constant(pngSubPath));
    }

    @Override
    public void animatedBackground(@NotNull String pngSubPathTemplate, int size, @NotNull Duration period) {
        Objects.requireNonNull(pngSubPathTemplate, "pngSubPathTemplate");
        Objects.requireNonNull(period, "period");

        lazyAnimatedBackground(GuiBackgroundFunction.sizedAnimation(pngSubPathTemplate, size), period);
    }

    @Override
    public void lazyBackground(@NotNull GuiBackgroundFunction function) {
        Objects.requireNonNull(function, "function");

        builder.lazyBackground(function);
        player2gui.values()
                .forEach(gui -> gui.getBackgroundController().setFunction(function));
    }

    @Override
    public void lazyAnimatedBackground(@NotNull GuiBackgroundFunction function, @NotNull Duration period) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");
        int periodTicks = period.getTicks();

        builder.lazyAnimatedBackground(function, period);
        player2gui.values()
                .forEach(gui -> gui.getBackgroundController().setFunctionWithChangePeriod(function, periodTicks));
    }
}
