package me.supcheg.advancedmanhunt.gui.impl.controller;

import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import me.supcheg.advancedmanhunt.packet.TitleSender;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DefaultAdvancedGuiController implements AdvancedGuiController, Listener, AutoCloseable {
    private final Map<String, DefaultAdvancedGui> key2gui = new HashMap<>();
    private final TextureWrapper textureWrapper;
    private final ButtonRenderer defaultButtonRenderer;
    private final TitleSender titleSender;
    private final BukkitTask task;

    public DefaultAdvancedGuiController(@NotNull TextureWrapper textureWrapper, @NotNull TitleSender titleSender,
                                        @NotNull Plugin plugin) {
        this.textureWrapper = textureWrapper;
        this.defaultButtonRenderer = ButtonRenderer.fromTextureWrapper(textureWrapper);
        this.titleSender = titleSender;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                key2gui.values().forEach(DefaultAdvancedGui::tick);
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void close() {
        task.cancel();
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    @NotNull
    @Contract("-> new")
    @Override
    public AdvancedGuiBuilder gui() {
        return new DefaultAdvancedGuiBuilder(this, textureWrapper, titleSender);
    }

    @NotNull
    @Contract("-> new")
    @Override
    public AdvancedButtonBuilder button() {
        return new DefaultAdvancedButtonBuilder(defaultButtonRenderer);
    }

    @Nullable
    @Override
    public AdvancedGui getGui(@NotNull String key) {
        return key2gui.get(key);
    }

    @NotNull
    @Contract("_ -> new")
    @Override
    public AdvancedGui buildAndRegister(@NotNull AdvancedGuiBuilder builder) {
        if (!(builder instanceof DefaultAdvancedGuiBuilder defaultAdvancedGuiBuilder)) {
            throw new IllegalArgumentException();
        }
        DefaultAdvancedGui gui = defaultAdvancedGuiBuilder.build();
        register(gui);

        return gui;
    }

    public void register(@NotNull DefaultAdvancedGui gui) {
        key2gui.put(gui.getKey(), gui);
    }

    @Override
    public void unregister(@NotNull String key) {
        key2gui.remove(key);
    }

    @Override
    public void unregister(@NotNull AdvancedGui gui) {
        if (gui instanceof DefaultAdvancedGui) {
            unregister(gui.getKey());
        }
        throw new IllegalArgumentException();
    }


    @EventHandler
    public void handleInventoryClick(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory != null && inventory.getHolder() instanceof AdvancedGuiHolder guiHolder) {
            guiHolder.getGui().handleClick(event);
        }
    }
}
