package me.supcheg.advancedmanhunt.gui.impl.inventory;

import com.google.errorprone.annotations.MustBeClosed;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.load.PreloadedAdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.key.KeyModifier;
import me.supcheg.advancedmanhunt.gui.impl.inventory.render.InventoryButtonRenderer;
import me.supcheg.advancedmanhunt.gui.impl.inventory.texture.TextureWrapper;
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

public class InventoryGuiController implements AdvancedGuiController, Listener, AutoCloseable {
    private final Map<String, InventoryGui> key2gui = new HashMap<>();
    private final Collection<String> keys = Collections.unmodifiableCollection(key2gui.keySet());
    private final Collection<AdvancedGui> guis = Collections.unmodifiableCollection(key2gui.values());
    private final TextureWrapper textureWrapper;
    @Getter
    private final InventoryButtonRenderer buttonRenderer;
    private final TitleSender titleSender;
    private final ContainerAdapter containerAdapter;
    private final BukkitTask task;

    public InventoryGuiController(@NotNull ItemStackWrapperFactory wrapperFactory,
                                  @NotNull TextureWrapper textureWrapper, @NotNull TitleSender titleSender,
                                  @NotNull ContainerAdapter containerAdapter, @NotNull Plugin plugin) {
        this.textureWrapper = textureWrapper;
        this.buttonRenderer = InventoryButtonRenderer.fromTextureWrapper(wrapperFactory, textureWrapper);
        this.titleSender = titleSender;
        this.containerAdapter = containerAdapter;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                key2gui.values().forEach(InventoryGui::tick);
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
                .registerTypeAdapterFactory(new JsonGuiSerializer(lookup))
                .create();

        AdvancedGuiBuilder builder;
        try (Reader reader = openResourceReader(resourcePath)) {
            builder = gson.fromJson(reader, AdvancedGuiBuilder.class);
        }
        builder.key(keyModifier.modify(builder.getKey(), key2gui.keySet()));

        return register(builder);
    }

    @Override
    public void saveResource(@NotNull AdvancedGui gui, @NotNull Writer writer) {
        new GsonBuilder()
                .registerTypeAdapterFactory(new JsonGuiSerializer(new ExceptionallyMethodHandleLookup()))
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()
                .toJson(gui.toBuilder(), AdvancedGuiBuilder.class, writer);
    }

    @MustBeClosed
    @NotNull
    @Contract("_ -> new")
    private Reader openResourceReader(@NotNull String resourcePath) throws IOException {
        return Files.newBufferedReader(containerAdapter.resolveResource(resourcePath));
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

    @NotNull
    @Contract("_ -> new")
    @Override
    public AdvancedGui register(@NotNull AdvancedGuiBuilder builder) {
        InventoryGuiHolder holder = new InventoryGuiHolder();
        InventoryGui gui = new InventoryGui(
                this,
                textureWrapper,
                titleSender,
                holder,
                builder
        );
        holder.setGui(gui);
        builder.getButtons().forEach(gui::addButton);
        key2gui.put(gui.getKey(), gui);
        return gui;
    }

    @Override
    public void unregister(@NotNull String key) {
        key2gui.remove(key);
    }

    @EventHandler
    public void handleInventoryClick(@NotNull InventoryClickEvent event) {
        InventoryGuiHolder holder = tryGetAdvancedGuiHolder(event.getClickedInventory());

        if (holder != null) {
            holder.getGui().handleClick(event);
        }
    }

    @EventHandler
    public void handleInventoryDrag(@NotNull InventoryDragEvent event) {
        InventoryGuiHolder holder = tryGetAdvancedGuiHolder(event.getView().getTopInventory());

        if (holder != null) {
            event.setCancelled(true);
        }
    }

    @Nullable
    @Contract("null -> null")
    private InventoryGuiHolder tryGetAdvancedGuiHolder(@Nullable Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof InventoryGuiHolder h ? h : null;
    }
}
