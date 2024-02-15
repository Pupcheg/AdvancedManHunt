package me.supcheg.advancedmanhunt.gui.impl.inventory;

import lombok.Getter;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiLoader;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.key.KeyModifier;
import me.supcheg.advancedmanhunt.gui.impl.inventory.render.InventoryButtonRenderer;
import me.supcheg.advancedmanhunt.gui.impl.inventory.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.util.TitleSender;
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
    private final AdvancedGuiLoader guiLoader;
    private final BukkitTask task;

    public InventoryGuiController(@NotNull ItemStackWrapperFactory wrapperFactory,
                                  @NotNull TextureWrapper textureWrapper, @NotNull TitleSender titleSender,
                                  @NotNull AdvancedGuiLoader guiLoader, @NotNull Plugin plugin) {
        this.textureWrapper = textureWrapper;
        this.buttonRenderer = InventoryButtonRenderer.fromTextureWrapper(wrapperFactory, textureWrapper);
        this.titleSender = titleSender;
        this.guiLoader = guiLoader;

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

    @SneakyThrows
    @NotNull
    @Override
    public AdvancedGui loadResource(@NotNull Object object, @NotNull String resourcePath, @NotNull KeyModifier keyModifier) {
        AdvancedGuiBuilder builder = guiLoader.loadResource(resourcePath);
        applyKeyModifier(builder, keyModifier);

        InventoryGui gui = build(builder, object);
        register(gui);
        return gui;
    }

    @SneakyThrows
    @Override
    public void saveResource(@NotNull AdvancedGui gui, @NotNull String path) {
        guiLoader.saveResource(gui.toBuilder(), path);
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
        InventoryGui gui = build(builder, null);
        register(gui);
        return gui;
    }

    private void applyKeyModifier(@NotNull AdvancedGuiBuilder builder, @NotNull KeyModifier keyModifier) {
        builder.key(keyModifier.modify(builder.getKey(), key2gui.keySet()));
    }

    @NotNull
    private InventoryGui build(@NotNull AdvancedGuiBuilder builder, @Nullable Object logicInstance) {
        InventoryGuiHolder holder = new InventoryGuiHolder();
        InventoryGui gui = new InventoryGui(
                this,
                textureWrapper,
                titleSender,
                holder,
                builder,
                logicInstance
        );
        holder.setGui(gui);
        builder.getButtons().stream()
                .peek(builder.getButtonConfigurer())
                .forEach(gui::addButton);
        return gui;
    }

    private void register(@NotNull InventoryGui gui) {
        key2gui.put(gui.getKey(), gui);
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
