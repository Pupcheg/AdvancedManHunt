package me.supcheg.advancedmanhunt.gui.impl.inventory.debug;

import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryButton;
import me.supcheg.advancedmanhunt.injector.Injector;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.player.Permission;
import me.supcheg.advancedmanhunt.text.Components;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.Component.empty;

@RequiredArgsConstructor
class InventoryButtonDebuggerImpl implements InventoryButtonDebugger {
    private final InventoryButton button;

    @Override
    public void handlePostClick(@NotNull InventoryClickEvent event) {
        if (!shouldHandleButtonDebug(event)) {
            return;
        }

        ItemStackWrapper wrapper = Injector.getBridge()
                .getItemStackWrapperFactory()
                .createItemStackWrapper();

        wrapper.setMaterial("minecraft:paper");
        wrapper.setEnchanted(true);
        wrapper.setTitle(empty());

        wrapper.setLore(List.of(
                key("//Slot"),
                value(event.getSlot()),
                empty(),

                key("//Name"),
                button.getNameController().getResource(),
                empty(),

                key("//Lore"),
                Component.join(JoinConfiguration.separator(value("//")), button.getLoreController().getResource()),
                empty(),

                key("//Texture"),
                value(button.getTextureController().getResource()),
                empty(),

                key("//Enable"),
                value(button.getEnableController().getState()),
                empty(),

                key("//Show"),
                value(button.getShowController().getState()),
                empty(),

                key("//Enchanted"),
                value(button.getEnchantedController().getState()),
                empty(),

                key("//Actions"),
                value(button.getClickActions().toString()),
                empty(),

                key("//Tickers"),
                key("//Start"),
                value(button.getTickConsumers().get(At.TICK_START).toString()),
                key("//End"),
                value(button.getTickConsumers().get(At.TICK_END).toString()),

                key("//Background"),
                value(button.getGui().getBackgroundController().getResource())
        ));

        ItemStackHolder snapshotHolder = wrapper.createSnapshotHolder();
        int slot = event.getSlot();
        Inventory inventory = Objects.requireNonNull(event.getClickedInventory(), "inventory");

        Bukkit.getScheduler().runTask(getPlugin(), () -> snapshotHolder.setAt(inventory, slot));
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    private Plugin getPlugin() {
        ConfiguredPluginClassLoader classLoader = (ConfiguredPluginClassLoader) getClass().getClassLoader();
        return Objects.requireNonNull(classLoader.getPlugin(), "plugin");
    }

    private boolean shouldHandleButtonDebug(@NotNull InventoryClickEvent event) {
        return event.getClick() == ClickType.SHIFT_RIGHT
                && event.getWhoClicked().hasPermission(Permission.DEBUG);
    }

    private static Component key(String key) {
        return Component.text()
                .content(key)
                .color(NamedTextColor.YELLOW)
                .apply(Components.noItalic())
                .build();
    }

    private static Component value(Object text) {
        return Component.text()
                .content(String.valueOf(text))
                .color(NamedTextColor.WHITE)
                .apply(Components.noItalic())
                .build();
    }
}
