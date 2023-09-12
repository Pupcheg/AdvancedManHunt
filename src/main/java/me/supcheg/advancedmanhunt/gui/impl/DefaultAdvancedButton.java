package me.supcheg.advancedmanhunt.gui.impl;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.impl.controller.BooleanController;
import me.supcheg.advancedmanhunt.gui.impl.controller.resource.ButtonResourceController;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultAdvancedButton implements AdvancedButton {
    @Getter
    private final DefaultAdvancedGui gui;
    private final BooleanController enableController;
    private final BooleanController showController;
    private final ButtonResourceController<ButtonTextureFunction, String> textureController;
    private final ButtonResourceController<ButtonNameFunction, Component> nameController;
    private final ButtonResourceController<ButtonLoreFunction, List<Component>> loreController;
    private final BooleanController enchantedController;
    private final Map<String, ButtonClickAction> key2clickActions;

    private final ButtonRenderer renderer;

    @Getter
    private boolean updated = true;

    public void tick(IntSet slots, Player player) {
        ButtonResourceGetContext ctx = new ButtonResourceGetContext(gui, this, slots, player);
        enableController.tick();
        showController.tick();
        textureController.tick(ctx);
        nameController.tick(ctx);
        loreController.tick(ctx);
        enchantedController.tick();

        updated =
                enableController.isUpdated() | showController.isUpdated() |
                textureController.isUpdated() | nameController.isUpdated() |
                loreController.isUpdated() | enchantedController.isUpdated();
    }

    public void handleClick(Player player, int slot) {
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
    public void enableFor(Duration duration) {
        enableController.setStateFor(true, duration.getTicks());
    }

    @Override
    public boolean isEnabled() {
        return enableController.isState();
    }

    @Override
    public Duration getEnabledDuration() {
        return enableController.isState() ? Duration.ofTicks(enableController.getTicksUntilStateSwap()) : Duration.INFINITY;
    }

    @Override
    public void disable() {
        enableController.setState(false);
    }

    @Override
    public void disableFor(Duration duration) {
        enableController.setStateFor(false, duration.getTicks());
    }

    @Override
    public boolean isDisabled() {
        return !enableController.isState();
    }

    @Override
    public Duration geDisabledDuration() {
        return !enableController.isState() ? Duration.ofTicks(enableController.getTicksUntilStateSwap()) : Duration.INFINITY;
    }

    @Override
    public void show() {
        showController.setState(true);
    }

    @Override
    public void showFor(Duration duration) {
        showController.setStateFor(true, duration.getTicks());
    }

    @Override
    public boolean isShown() {
        return showController.isState();
    }

    @Override
    public Duration getShownDuration() {
        return showController.isState() ? Duration.ofTicks(showController.getTicksUntilStateSwap()) : Duration.INFINITY;
    }

    @Override
    public void hide() {
        showController.setState(false);
    }

    @Override
    public void hideFor(Duration duration) {
        showController.setStateFor(false, duration.getTicks());
    }

    @Override
    public boolean isHidden() {
        return !showController.isState();
    }

    @Override
    public Duration getHiddenDuration() {
        return !showController.isState() ? Duration.ofTicks(showController.getTicksUntilStateSwap()) : Duration.INFINITY;
    }

    @Override
    public void removeFromAllSlots() {
        gui.removeButtonFromAllSlots(this);
    }

    @Override
    public void addClickAction(String key, ButtonClickAction action) {
        key2clickActions.put(key, action);
    }

    @Override
    public boolean hasClickAction(String key) {
        return key2clickActions.containsKey(key);
    }

    @Override
    public boolean removeClickAction(String key) {
        return key2clickActions.remove(key) != null;
    }

    @Override
    public Collection<ButtonClickAction> getClickActions() {
        return key2clickActions.values();
    }

    @Override
    public void setTexture(String resourceJsonPath) {
        Objects.requireNonNull(resourceJsonPath, "resourceJsonPath");
        textureController.setFunction(ButtonTextureFunction.constant(resourceJsonPath));
    }

    @Override
    public void lazyTexture(ButtonTextureFunction function) {
        Objects.requireNonNull(function, "function");
        textureController.setFunction(function);
    }

    @Override
    public void setName(Component name) {
        Objects.requireNonNull(name, "name");
        nameController.setFunction(ButtonNameFunction.constant(name));
    }

    @Override
    public void lazyName(ButtonNameFunction function) {
        Objects.requireNonNull(function, "function");
        nameController.setFunction(function);
    }

    @Override
    public void animatedName(Duration period, ButtonNameFunction function) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");
        nameController.setFunctionWithChangePeriod(function, period.getTicks());
    }

    @Override
    public void setLore(List<Component> lore) {
        Objects.requireNonNull(lore, "lore");
        loreController.setFunction(ButtonLoreFunction.constant(lore));
    }

    @Override
    public void lazyLore(ButtonLoreFunction function) {
        Objects.requireNonNull(function, "function");
        loreController.setFunction(function);
    }

    @Override
    public void animatedLore(Duration period, ButtonLoreFunction function) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");
        loreController.setFunctionWithChangePeriod(function, period.getTicks());
    }

    @Override
    public boolean isEnchanted() {
        return enchantedController.isState();
    }

    @Override
    public void setEnchanted(boolean value) {
        enchantedController.setState(value);
    }

    public ItemStack render() {
        if (isHidden()) {
            return null;
        }

        return renderer.render(
                textureController.getResource(),
                nameController.getResource(),
                loreController.getResource(),
                enchantedController.isState()
        );
    }
}
