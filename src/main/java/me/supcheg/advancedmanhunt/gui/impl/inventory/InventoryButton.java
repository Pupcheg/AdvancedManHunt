package me.supcheg.advancedmanhunt.gui.impl.inventory;

import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.ButtonInteractType;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.impl.common.Button;
import me.supcheg.advancedmanhunt.gui.impl.inventory.debug.InventoryButtonDebugger;
import me.supcheg.advancedmanhunt.gui.impl.inventory.render.InventoryButtonRenderer;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class InventoryButton extends Button {
    private final InventoryGui gui;
    private final InventoryButtonDebugger debug;

    InventoryButton(@NotNull InventoryGui gui, int slot, @NotNull AdvancedButtonBuilder builder) {
        super(gui, slot, builder);
        this.gui = gui;
        this.debug = InventoryButtonDebugger.create(this);
    }

    @NotNull
    public ItemStackHolder render() {
        InventoryButtonRenderer renderer = gui.getController().getButtonRenderer();
        return isHidden() ? renderer.emptyHolder() : renderer.render(this);
    }

    public void tick() {
        acceptAllConsumersWithAt(At.TICK_START, context);
        acceptAllConsumersWithAt(At.TICK_END, context);
        updateUpdated();
    }

    public void handleClick(@NotNull InventoryClickEvent event) {
        handleClick(wrapEvent(event));
        debug.handlePostClick(event);
    }

    @NotNull
    private ButtonClickContext wrapEvent(@NotNull InventoryClickEvent event) {
        ButtonInteractType interactType = switch (event.getClick()) {
            case RIGHT -> ButtonInteractType.RIGHT_CLICK;
            case SHIFT_RIGHT -> ButtonInteractType.SHIFT_RIGHT_CLICK;
            case SHIFT_LEFT -> ButtonInteractType.SHIFT_LEFT_CLICK;
            default -> ButtonInteractType.LEFT_CLICK;
        };

        return new ButtonClickContext(gui, this, slot, interactType, (Player) event.getWhoClicked());
    }
}
