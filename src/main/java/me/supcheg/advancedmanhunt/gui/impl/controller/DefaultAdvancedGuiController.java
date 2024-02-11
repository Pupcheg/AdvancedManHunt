package me.supcheg.advancedmanhunt.gui.impl.controller;

import com.google.errorprone.annotations.MustBeClosed;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.load.PreloadedAdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.key.KeyModifier;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.AdvancedGuiHolder;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.json.JsonGuiSerializer;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.TitleSender;
import me.supcheg.advancedmanhunt.util.reflect.ExceptionallyMethodHandleLookup;
import me.supcheg.advancedmanhunt.util.reflect.FutureMethodHandleLookup;
import me.supcheg.advancedmanhunt.util.reflect.InstantMethodHandleLookup;
import me.supcheg.advancedmanhunt.util.reflect.MethodHandleLookup;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultAdvancedGuiController implements AdvancedGuiController, Listener, AutoCloseable {
    private final Map<String, DefaultAdvancedGui> key2gui = new HashMap<>();
    private final Collection<String> keys = Collections.unmodifiableCollection(key2gui.keySet());
    private final Collection<AdvancedGui> guis = Collections.unmodifiableCollection(key2gui.values());
    private final TextureWrapper textureWrapper;
    private final ButtonRenderer defaultButtonRenderer;
    private final TitleSender titleSender;
    private final ContainerAdapter containerAdapter;
    private final BukkitTask task;

    public DefaultAdvancedGuiController(@NotNull ItemStackWrapperFactory wrapperFactory,
                                        @NotNull TextureWrapper textureWrapper, @NotNull TitleSender titleSender,
                                        @NotNull ContainerAdapter containerAdapter, @NotNull Plugin plugin) {
        this.textureWrapper = textureWrapper;
        this.defaultButtonRenderer = ButtonRenderer.fromTextureWrapper(wrapperFactory, textureWrapper);
        this.titleSender = titleSender;
        this.containerAdapter = containerAdapter;

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
    @Override
    public AdvancedGui loadResource(@NotNull Object logicClass, @NotNull String resourcePath, @NotNull KeyModifier keyModifier) {
        InstantMethodHandleLookup lookup = new InstantMethodHandleLookup(logicClass);
        AdvancedGui gui = loadResource(lookup, resourcePath, keyModifier);
        lookup.logIfHasUnusedMethods();
        return gui;
    }

    @NotNull
    @Override
    public PreloadedAdvancedGui preloadResource(@NotNull String resourcePath, @NotNull KeyModifier keyModifier) {
        FutureMethodHandleLookup futureLookup = new FutureMethodHandleLookup();
        AdvancedGui gui = loadResource(futureLookup, resourcePath, keyModifier);

        return o -> {
            InstantMethodHandleLookup lookup = new InstantMethodHandleLookup(o);
            futureLookup.initializeAllWith(lookup);
            lookup.logIfHasUnusedMethods();
            return gui;
        };
    }

    @SneakyThrows
    @NotNull
    private AdvancedGui loadResource(@NotNull MethodHandleLookup lookup, @NotNull String resourcePath,
                                     @NotNull KeyModifier keyModifier) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new JsonGuiSerializer(this, lookup))
                .create();

        AdvancedGuiBuilder builder;
        try (Reader reader = openResourceReader(resourcePath)) {
            builder = gson.fromJson(reader, AdvancedGuiBuilder.class);
        }
        builder.key(keyModifier.modify(builder.getKey(), key2gui.keySet()));

        return builder.buildAndRegister();
    }

    @Override
    public void saveResource(@NotNull AdvancedGui gui, @NotNull Writer writer) {
        new GsonBuilder()
                .registerTypeAdapterFactory(new JsonGuiSerializer(this, new ExceptionallyMethodHandleLookup()))
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()
                .toJson(gui, AdvancedGui.class, writer);
    }

    @MustBeClosed
    @NotNull
    @Contract("_ -> new")
    private Reader openResourceReader(@NotNull String resourcePath) throws IOException {
        return Files.newBufferedReader(containerAdapter.resolveResource(resourcePath));
    }

    @NotNull
    @Contract("-> new")
    @Override
    public DefaultAdvancedGuiBuilder gui() {
        return new DefaultAdvancedGuiBuilder(this, textureWrapper, titleSender);
    }

    @NotNull
    @Contract("-> new")
    @Override
    public DefaultAdvancedButtonBuilder button() {
        return new DefaultAdvancedButtonBuilder(defaultButtonRenderer);
    }

    @NotNull
    @Override
    public Collection<String> getRegisteredKeys() {
        return keys;
    }

    @NotNull
    @Override
    public Collection<AdvancedGui> getRegisteredGuis() {
        return guis;
    }

    @Nullable
    @Override
    public AdvancedGui getGui(@NotNull String key) {
        return key2gui.get(key);
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
            key2gui.remove(gui.getKey(), gui);
        }
        throw new IllegalArgumentException();
    }

    @Nullable
    @Contract("null -> null")
    private AdvancedGuiHolder tryGetAdvancedGuiHolder(@Nullable Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof AdvancedGuiHolder h ? h : null;
    }

    @EventHandler
    public void handleInventoryClick(@NotNull InventoryClickEvent event) {
        AdvancedGuiHolder holder = tryGetAdvancedGuiHolder(event.getClickedInventory());

        if (holder != null) {
            holder.getGui().handleClick(event);
        }
    }

    @EventHandler
    public void handleInventoryDrag(@NotNull InventoryDragEvent event) {
        AdvancedGuiHolder holder = tryGetAdvancedGuiHolder(event.getView().getTopInventory());

        if (holder != null) {
            event.setCancelled(true);
        }
    }
}
