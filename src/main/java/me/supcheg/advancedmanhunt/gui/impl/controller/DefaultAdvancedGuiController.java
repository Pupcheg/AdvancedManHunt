package me.supcheg.advancedmanhunt.gui.impl.controller;

import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedGuiBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DefaultAdvancedGuiController implements AdvancedGuiController, Listener, AutoCloseable {
    private final List<DefaultAdvancedGui> guiList = new ArrayList<>();
    private final BukkitTask task;

    public DefaultAdvancedGuiController(@NotNull Plugin plugin, @NotNull EventListenerRegistry eventListenerRegistry) {
        eventListenerRegistry.addListener(this);
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                guiList.forEach(DefaultAdvancedGui::tick);
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void close() {
        task.cancel();
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    @Override
    public AdvancedGuiBuilder gui() {
        return new DefaultAdvancedGuiBuilder();
    }

    @Override
    public AdvancedButtonBuilder button() {
        return new DefaultAdvancedButtonBuilder();
    }


    @Override
    public AdvancedGui buildAndRegister(AdvancedGuiBuilder builder) {
        if (!(builder instanceof DefaultAdvancedGuiBuilder defaultAdvancedGuiBuilder)) {
            throw new IllegalArgumentException();
        }
        DefaultAdvancedGui gui = defaultAdvancedGuiBuilder.build();
        guiList.add(gui);

        System.out.println(gui);
        return gui;
    }

    @Override
    public void unregister(AdvancedGui gui) {
        if (!(gui instanceof DefaultAdvancedGui defaultAdvancedGui)) {
            throw new IllegalArgumentException();
        }
        guiList.remove(defaultAdvancedGui);
    }


    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory != null && inventory.getHolder() instanceof AdvancedGuiHolder guiHolder) {
            guiHolder.getGui().handleClick(event);
        }
    }

    @EventHandler
    public void handleInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof AdvancedGuiHolder guiHolder) {
            guiHolder.getGui().handleClose(event);
        }
    }
}
