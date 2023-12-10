package me.supcheg.advancedmanhunt.gui.impl;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import me.supcheg.advancedmanhunt.gui.impl.controller.BooleanController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import me.supcheg.advancedmanhunt.gui.impl.wrapped.WrappedButtonClickAction;
import me.supcheg.advancedmanhunt.gui.impl.wrapped.WrappedButtonTickConsumer;
import me.supcheg.bridge.item.ItemStackHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CustomLog
@RequiredArgsConstructor
public class DefaultAdvancedButton implements AdvancedButton {
    private final AdvancedGui gui;
    private final BooleanController enableController;
    private final BooleanController showController;
    private final ResourceController<ButtonTextureFunction, ButtonResourceGetContext, String> textureController;
    private final ResourceController<ButtonNameFunction, ButtonResourceGetContext, Component> nameController;
    private final ResourceController<ButtonLoreFunction, ButtonResourceGetContext, List<Component>> loreController;
    private final BooleanController enchantedController;
    private final List<WrappedButtonClickAction> clickActions;
    private final Map<At, List<WrappedButtonTickConsumer>> tickConsumers;
    private final ButtonRenderer renderer;
    private boolean updated = true;

    public void tick(int slot) {
        ButtonResourceGetContext ctx = new ButtonResourceGetContext(gui, this, slot);

        acceptAllConsumersWithAt(At.TICK_START, ctx);

        enableController.tick();
        showController.tick();
        textureController.tick(ctx);
        nameController.tick(ctx);
        loreController.tick(ctx);
        enchantedController.tick();

        acceptAllConsumersWithAt(At.TICK_END, ctx);

        updated = updated |
                enableController.isUpdated() | showController.isUpdated() |
                textureController.isUpdated() | nameController.isUpdated() |
                loreController.isUpdated() | enchantedController.isUpdated();
    }

    public boolean isUpdated() {
        boolean value = updated;
        updated = false;
        return value;
    }

    private void acceptAllConsumersWithAt(@NotNull At at, @NotNull ButtonResourceGetContext ctx) {
        for (WrappedButtonTickConsumer tickConsumer : tickConsumers.get(at)) {
            try {
                tickConsumer.accept(ctx);
            } catch (Exception e) {
                log.error("An error occurred while accepting tick consumer", e);
            }
        }
    }

    public void handleClick(@NotNull InventoryClickEvent event) {
        if (isDisabled() || isHidden()) {
            return;
        }

        if (clickActions.isEmpty()) {
            return;
        }

        ButtonClickContext ctx = new ButtonClickContext(event, gui, this, event.getSlot(), (Player) event.getWhoClicked());

        for (WrappedButtonClickAction action : clickActions) {
            try {
                action.accept(ctx);
            } catch (Exception e) {
                log.error("An error occurred while handling click to action", e);
            }
        }
    }

    @Override
    public void enableState(boolean value) {
        enableController.setState(value);
    }

    @Override
    public void enableFor(@NotNull Duration duration) {
        enableController.setStateFor(true, duration.getTicks());
    }

    @Override
    public boolean isEnabled() {
        return enableController.getState();
    }

    @NotNull
    @Override
    public Duration getEnabledDuration() {
        return enableController.getState() ? Duration.ofTicks(enableController.getTicksUntilStateSwap()) : Duration.INFINITY;
    }

    @Override
    public void disableFor(@NotNull Duration duration) {
        enableController.setStateFor(false, duration.getTicks());
    }

    @Override
    public boolean isDisabled() {
        return !enableController.getState();
    }

    @NotNull
    @Override
    public Duration geDisabledDuration() {
        return !enableController.getState() ? Duration.ofTicks(enableController.getTicksUntilStateSwap()) : Duration.INFINITY;
    }

    @Override
    public void show() {
        showController.setState(true);
    }

    @Override
    public void showFor(@NotNull Duration duration) {
        showController.setStateFor(true, duration.getTicks());
    }

    @Override
    public boolean isShown() {
        return showController.getState();
    }

    @NotNull
    @Override
    public Duration getShownDuration() {
        return showController.getState() ? Duration.ofTicks(showController.getTicksUntilStateSwap()) : Duration.INFINITY;
    }

    @Override
    public void hide() {
        showController.setState(false);
    }

    @Override
    public void hideFor(@NotNull Duration duration) {
        showController.setStateFor(false, duration.getTicks());
    }

    @Override
    public boolean isHidden() {
        return !showController.getState();
    }

    @NotNull
    @Override
    public Duration getHiddenDuration() {
        return !showController.getState() ? Duration.ofTicks(showController.getTicksUntilStateSwap()) : Duration.INFINITY;
    }

    @Override
    public void addClickAction(@NotNull Priority priority, @NotNull ButtonClickAction action) {
        clickActions.add(new WrappedButtonClickAction(priority, action));
    }

    @NotNull
    @Override
    public Collection<? extends ButtonClickAction> getClickActions() {
        return clickActions;
    }

    @Override
    public void setTexture(@NotNull ButtonTextureFunction function) {
        Objects.requireNonNull(function, "function");
        textureController.setFunction(function);
    }

    @Override
    public void setName(@NotNull ButtonNameFunction function) {
        Objects.requireNonNull(function, "function");
        nameController.setFunction(function);
    }

    @Override
    public void setAnimatedName(@NotNull Duration period, @NotNull ButtonNameFunction function) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");
        nameController.setFunctionWithChangePeriod(function, period);
    }

    @Override
    public void setLore(@NotNull ButtonLoreFunction function) {
        Objects.requireNonNull(function, "function");
        loreController.setFunction(function);
    }

    @Override
    public void setAnimatedLore(@NotNull Duration period, @NotNull ButtonLoreFunction function) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");
        loreController.setFunctionWithChangePeriod(function, period);
    }

    @Override
    public boolean isEnchanted() {
        return enchantedController.getState();
    }

    @Override
    public void setEnchanted(boolean value) {
        enchantedController.setState(value);
    }

    @Override
    public void setEnchantedFor(boolean value, @NotNull Duration duration) {
        Objects.requireNonNull(duration, "duration");
        enableController.setStateFor(value, duration.getTicks());
    }

    @NotNull
    public ItemStackHolder render() {
        return isHidden() ?
                renderer.emptyHolder() :
                renderer.render(
                        textureController.getResource(),
                        nameController.getResource(),
                        loreController.getResource(),
                        enchantedController.getState()
                );
    }
}
