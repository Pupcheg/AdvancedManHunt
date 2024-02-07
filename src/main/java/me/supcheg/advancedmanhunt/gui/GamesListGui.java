package me.supcheg.advancedmanhunt.gui;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.ManHuntGameEvent;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.text.GuiText;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.NAMESPACE;

public class GamesListGui implements Listener {
    public static final String KEY = NAMESPACE + ":games_list";

    private final ManHuntGameRepository repository;
    private final ManHuntGame[] games;
    private boolean updated;

    public GamesListGui(@NotNull ManHuntGameRepository repository, @NotNull EventListenerRegistry registry) {
        this.repository = repository;
        this.games = new ManHuntGame[18];
        this.updated = true;

        registry.addListener(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onManHuntGameEvent(@NotNull ManHuntGameEvent event) {
        copyRange(repository.getEntities(), games);
        updated = true;
    }

    @SneakyThrows
    public void load(@NotNull AdvancedGuiController controller) {
        controller.loadResource(this, "gui/games_list.json");
    }

    @SuppressWarnings("unused")
    private void acceptGameButtonClick(@NotNull ButtonClickContext ctx) {
        ManHuntGame game = getGameFromSlot(ctx.getSlot());
        ctx.getPlayer().performCommand(NAMESPACE + " game join " + game.getUniqueId());
    }

    @SuppressWarnings("unused")
    private void acceptGameButtonTickEnd(@NotNull ButtonResourceGetContext ctx) {
        if (updated) {
            ManHuntGame game = getGameFromSlot(ctx.getSlot());
            AdvancedButton button = ctx.getButton();

            if (game != null) {
                button.show();
                button.setLore(
                        GuiText.GAMES_LIST_GAME_STATE.build(game.getState()),
                        GuiText.GAMES_LIST_GAME_PLAYERS_COUNT.build(game.getPlayers().size()),
                        GuiText.GAMES_LIST_GAME_OWNER.build(game.getOwner()),
                        GuiText.GAMES_LIST_GAME_UNIQUE_ID.build(game.getUniqueId())
                );
            } else {
                button.hide();
            }
        }
    }

    @SuppressWarnings("unused")
    private void acceptGuiTickEnd(@NotNull GuiResourceGetContext ctx) {
        updated = false;
    }

    private ManHuntGame getGameFromSlot(int slot) {
        return games[slot - slot / 9 * 6 - 6];
    }

    private static <T> void copyRange(@NotNull Iterable<T> src, T @NotNull [] dst) {
        int index = 0;
        for (T t : src) {
            dst[index++] = t;
            if (index == dst.length) {
                return;
            }
        }
        Arrays.fill(dst, index, dst.length, null);
    }

}
