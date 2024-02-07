package me.supcheg.advancedmanhunt.gui;

import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Game.ConfigDefaults;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Game.ConfigLimits;
import me.supcheg.advancedmanhunt.config.IntLimit;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.key.DefaultKeyModifier;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.NAMESPACE;

public class ConfigurateGameGui {
    public static final String KEY = NAMESPACE + ":configurate_game";

    private final AdvancedGuiController controller;
    private final String currentKey;
    private final ManHuntGame game;

    private boolean updatedMaxHunters;
    private boolean updatedMaxSpectators;

    public ConfigurateGameGui(@NotNull AdvancedGuiController controller, @NotNull ManHuntGame game) {
        this.controller = controller;
        this.game = game;
        this.currentKey = controller.loadResource(this, "gui/configurate_game.json", DefaultKeyModifier.ADDITIONAL_HASH)
                .getKey();

        this.updatedMaxHunters = true;
        this.updatedMaxSpectators = true;
    }

    public void open(@NotNull Player player) {
        controller.getGuiOrThrow(currentKey).open(player);
    }

    public void tickMaxHunters(@NotNull ButtonResourceGetContext ctx) {
        if (updatedMaxHunters) {
            ctx.getButton().setName(Component.text(game.getConfig().getMaxHunters()));
            updatedMaxHunters = false;
        }
    }

    public void handleModifyMaxHunters(@NotNull ButtonClickContext ctx) {
        ClickType clickType = ctx.getEvent().getClick();

        final int oldValue = game.getConfig().getMaxHunters();
        int value = modifyValue(clickType, oldValue, ConfigDefaults.MAX_HUNTERS);
        value = applyLimits(value, ConfigLimits.MAX_HUNTERS);

        if (oldValue != value) {
            game.getConfig().setMaxHunters(value);
            updatedMaxHunters = true;
        }

    }

    public void tickMaxSpectators(@NotNull ButtonResourceGetContext ctx) {
        if (updatedMaxSpectators) {
            ctx.getButton().setName(Component.text(game.getConfig().getMaxSpectators()));
            updatedMaxSpectators = false;
        }
    }

    public void handleModifyMaxSpectators(@NotNull ButtonClickContext ctx) {
        ClickType clickType = ctx.getEvent().getClick();

        final int oldValue = game.getConfig().getMaxSpectators();
        int value = modifyValue(clickType, oldValue, ConfigDefaults.MAX_SPECTATORS);
        value = applyLimits(value, ConfigLimits.MAX_SPECTATORS);

        if (oldValue != value) {
            game.getConfig().setMaxSpectators(value);
            updatedMaxSpectators = true;
        }

    }

    private int modifyValue(ClickType clickType, int value, int defaultValue) {
        switch (clickType) {
            case LEFT -> value++;
            case SHIFT_LEFT -> value += 5;
            case RIGHT -> value--;
            case SHIFT_RIGHT -> value -= 5;
            case MIDDLE -> value = defaultValue;
        }
        return value;
    }

    private int applyLimits(int value, IntLimit limits) {
        return Math.max(limits.getMinValue(), Math.min(value, limits.getMaxValue()));
    }
}
