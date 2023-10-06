package me.supcheg.advancedmanhunt.gui.impl;

import lombok.Getter;
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
import me.supcheg.advancedmanhunt.gui.impl.controller.BooleanController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultAdvancedButton implements AdvancedButton {
    private final AdvancedGui gui;
    private final BooleanController enableController;
    private final BooleanController showController;
    private final ResourceController<ButtonTextureFunction, ButtonResourceGetContext, String> textureController;
    private final ResourceController<ButtonNameFunction, ButtonResourceGetContext, Component> nameController;
    private final ResourceController<ButtonLoreFunction, ButtonResourceGetContext, List<Component>> loreController;
    private final BooleanController enchantedController;
    private final Map<String, ButtonClickAction> key2clickActions;
    private final ButtonRenderer renderer;
    @Getter
    private boolean updated = true;

    public void tick(int slot, @Nullable Player player) {
        ButtonResourceGetContext ctx = new ButtonResourceGetContext(gui, this, slot, player);
        enableController.tick();
        showController.tick();
        textureController.tick(ctx);
        nameController.tick(ctx);
        loreController.tick(ctx);
        enchantedController.tick();

        updated = updated |
                enableController.isUpdated() | showController.isUpdated() |
                textureController.isUpdated() | nameController.isUpdated() |
                loreController.isUpdated() | enchantedController.isUpdated();
    }

    public void handleClick(@NotNull Player player, int slot) {
        if (isDisabled() || isHidden()) {
            return;
        }

        Collection<ButtonClickAction> actions = key2clickActions.values();
        if (actions.isEmpty()) {
            return;
        }

        ButtonClickContext ctx = new ButtonClickContext(gui, this, slot, player);

        for (ButtonClickAction action : actions) {
            action.accept(ctx);
        }
    }

    @Override
    public void enable() {
        enableController.setState(true);
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
    public void disable() {
        enableController.setState(false);
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
    public void addClickAction(@NotNull String key, @NotNull ButtonClickAction action) {
        key2clickActions.put(key, action);
    }

    @Override
    public boolean hasClickAction(@NotNull String key) {
        return key2clickActions.containsKey(key);
    }

    @Override
    public boolean removeClickAction(@NotNull String key) {
        return key2clickActions.remove(key) != null;
    }

    @NotNull
    @Override
    public Collection<ButtonClickAction> getClickActions() {
        return key2clickActions.values();
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

    @Nullable
    public ItemStack render() {
        if (isHidden()) {
            return null;
        }

        return renderer.render(
                textureController.getResource(),
                nameController.getResource(),
                loreController.getResource(),
                enchantedController.getState()
        );
    }
}
