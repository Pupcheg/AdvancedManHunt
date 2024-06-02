package me.supcheg.advancedmanhunt.game.gui;

import lombok.Getter;
import me.supcheg.advancedmanhunt.config.IntLimit;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.ButtonInteractType;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import me.supcheg.advancedmanhunt.gui.api.key.DefaultKeyModifier;
import me.supcheg.advancedmanhunt.reflect.ReflectCalled;
import me.supcheg.advancedmanhunt.text.GuiText;
import me.supcheg.advancedmanhunt.util.Keys;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

public class ManHuntGameConfigureGui {
    public static final Key KEY = Keys.advancedmanhuntKey("manhunt_game_configure");

    @Getter
    private final AdvancedGui gui;
    private final ManHuntGame game;
    private final ManHuntGameConfiguration config;

    private boolean updateMaxHunters;
    private boolean updateMaxSpectators;

    public ManHuntGameConfigureGui(@NotNull AdvancedGuiController controller, @NotNull ManHuntGame game) {
        this.game = game;
        this.gui = controller.loadResource(this, "gui/manhunt_game_configure.json", DefaultKeyModifier.ADDITIONAL_HASH);
        this.config = new ManHuntGameConfiguration();

        this.updateMaxHunters = true;
        this.updateMaxSpectators = true;
    }

    public void open(@NotNull Player player) {
        discardChanges();
        gui.open(player);
    }

    public void unregister() {
        gui.getController().unregister(gui.getKey());
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
        int value = modifyValue(interactType, oldValue, config().game.configLimits.maxHunters);

        if (oldValue != value) {
            config.setMaxHunters(value);
            updateMaxHunters = true;
        }
    }

    @ReflectCalled
    public void tickMaxHunters(@NotNull ButtonTickContext ctx) {
        if (updateMaxHunters) {
            ctx.getButton().setLore(
                    GuiText.CONFIGURE_GAME_CURRENT_VALUE.build(config.getMaxHunters())
            );
            updateMaxHunters = false;
        }
    }

    @ReflectCalled
    public void handleModifyMaxSpectators(@NotNull ButtonClickContext ctx) {
        ButtonInteractType interactType = ctx.getInteractType();

        int oldValue = config.getMaxSpectators();
        int value = modifyValue(interactType, oldValue, config().game.configLimits.maxSpectators);

        if (oldValue != value) {
            config.setMaxSpectators(value);
            updateMaxSpectators = true;
        }
    }

    @ReflectCalled
    public void tickMaxSpectators(@NotNull ButtonTickContext ctx) {
        if (updateMaxSpectators) {
            ctx.getButton().setLore(
                    GuiText.CONFIGURE_GAME_CURRENT_VALUE.build(config.getMaxSpectators())
            );
            updateMaxSpectators = false;
        }
    }

    private int modifyValue(@NotNull ButtonInteractType interactType, int value, @NotNull IntLimit limit) {
        return limit.apply(switch (interactType) {
            case LEFT_CLICK -> value + 1;
            case SHIFT_LEFT_CLICK -> value + 5;
            case RIGHT_CLICK -> value - 1;
            case SHIFT_RIGHT_CLICK -> value - 5;
        });
    }
}
