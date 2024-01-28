package me.supcheg.advancedmanhunt.gui.impl.debug;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedButton;
import me.supcheg.advancedmanhunt.player.Permission;
import me.supcheg.bridge.BridgeHolder;
import me.supcheg.bridge.item.ItemStackWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

@RequiredArgsConstructor
public class DefaultButtonDebugger implements ButtonDebugger {
    private final DefaultAdvancedButton button;

    @Override
    public void handlePostClick(@NotNull InventoryClickEvent event) {
        if (!shouldHandleButtonDebug(event)) {
            return;
        }

        ItemStackWrapper wrapper = BridgeHolder.getInstance()
                .getItemStackWrapperFactory()
                .createItemStackWrapper();

        wrapper.setMaterial("minecraft:paper");
        wrapper.setEnchanted(true);
        wrapper.setTitle(Component.empty());

        wrapper.setLore(List.of(
                text("//Slot"),
                text(event.getSlot()),

                text("//Name"),
                text(button.getNameController().toString()),
                button.getNameController().getResource(),

                text("//Lore"),
                text(button.getLoreController().toString()),
                Component.join(JoinConfiguration.separator(text("\\n")), button.getLoreController().getResource()),

                text("//Texture"),
                text(button.getTextureController().toString()),
                text(button.getTextureController().getResource()),

                text("//Enable"),
                text(button.getEnableController().toString()),
                text(button.getEnableController().getState()),

                text("//Show"),
                text(button.getShowController().toString()),
                text(button.getShowController().getState()),

                text("//Enchanted"),
                text(button.getEnchantedController().toString()),
                text(button.getEnchantedController().getState()),

                text("//Actions"),
                text(button.getClickActions().toString()),

                text("//Tickers"),
                text("//Start"),
                text(button.getTickConsumers().get(At.TICK_START).toString()),
                text("//End"),
                text(button.getTickConsumers().get(At.TICK_END).toString())
        ));

        wrapper.createSnapshotHolder()
                .sendAt((Player) event.getWhoClicked(), event.getRawSlot());
    }

    private boolean shouldHandleButtonDebug(@NotNull InventoryClickEvent event) {
        return event.getClick() == ClickType.NUMBER_KEY
                && event.getHotbarButton() == 8
                && event.getWhoClicked().hasPermission(Permission.DEBUG);
    }
}
