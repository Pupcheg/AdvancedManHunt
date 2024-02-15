package me.supcheg.advancedmanhunt.gui;

import lombok.Getter;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Game.ConfigLimits;
import me.supcheg.advancedmanhunt.config.IntLimit;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.ButtonInteractType;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import me.supcheg.advancedmanhunt.gui.api.key.DefaultKeyModifier;
import me.supcheg.advancedmanhunt.text.GuiText;
import me.supcheg.advancedmanhunt.util.reflect.ReflectCalled;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.NAMESPACE;

public class ConfigurateGameGui {
    public static final String KEY = NAMESPACE + ":configurate_game";

    private final AdvancedGuiController controller;
    @Getter
    private final String currentKey;
    private final ManHuntGame game;
    private final ManHuntGameConfiguration config;

    private boolean updateMaxHunters;
    private boolean updateMaxSpectators;

    public ConfigurateGameGui(@NotNull AdvancedGuiController controller, @NotNull ManHuntGame game) {
        this.controller = controller;
        this.game = game;
        this.currentKey = controller.loadResource(this, "gui/configurate_game.json", DefaultKeyModifier.ADDITIONAL_HASH)
                .getKey();
        this.config = new ManHuntGameConfiguration();

        this.updateMaxHunters = true;
        this.updateMaxSpectators = true;
    }

    public void open(@NotNull Player player) {
        discardChanges();
        controller.getGuiOrThrow(currentKey).open(player);
    }

    @ReflectCalled
    public void handleDiscard(@NotNull ButtonClickContext ctx) {
        discardChanges();
    }

    private void discardChanges() {
        config.merge(game.getConfig());

        updateMaxHunters = true;
        updateMaxSpectators = true;
    }

    @ReflectCalled
    public void handleSave(@NotNull ButtonClickContext ctx) {
        game.getConfig().merge(config);
    }

    @ReflectCalled
    public void handleModifyMaxHunters(@NotNull ButtonClickContext ctx) {
        ButtonInteractType interactType = ctx.getInteractType();

        int oldValue = config.getMaxHunters();
        int value = modifyValue(interactType, oldValue, AdvancedManHuntConfig.get().game.configLimits.maxHunters);

        if (oldValue != value) {
            config.setMaxHunters(value);
            updateMaxHunters = true;
        }
    }

    @ReflectCalled
    public void tickMaxHunters(@NotNull ButtonTickContext ctx) {
        if (updateMaxHunters) {
            ctx.getButton().setLore(
                    GuiText.CONFIGURATE_GAME_CURRENT_VALUE.build(config.getMaxHunters())
            );
            updateMaxHunters = false;
        }
    }

    @ReflectCalled
    public void handleModifyMaxSpectators(@NotNull ButtonClickContext ctx) {
        ButtonInteractType interactType = ctx.getInteractType();

        int oldValue = config.getMaxSpectators();
        int value = modifyValue(interactType, oldValue, AdvancedManHuntConfig.get().game.configLimits.maxSpectators);

        if (oldValue != value) {
            config.setMaxSpectators(value);
            updateMaxSpectators = true;
        }
    }

    @ReflectCalled
    public void tickMaxSpectators(@NotNull ButtonTickContext ctx) {
        if (updateMaxSpectators) {
            ctx.getButton().setLore(
                    GuiText.CONFIGURATE_GAME_CURRENT_VALUE.build(config.getMaxSpectators())
            );
            updateMaxSpectators = false;
        }
    }

    private int modifyValue(@NotNull ButtonInteractType interactType, int value, @NotNull IntLimit limit) {
        return limit.apply(switch (interactType) {
            case LEFT_CLICK -> value + 1;
            case RIGHT_CLICK -> value - 1;
        });
    }
}
