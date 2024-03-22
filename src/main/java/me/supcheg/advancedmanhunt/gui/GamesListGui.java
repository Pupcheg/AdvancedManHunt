package me.supcheg.advancedmanhunt.gui;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.event.ManHuntGameCreateEvent;
import me.supcheg.advancedmanhunt.event.ManHuntGameStartEvent;
import me.supcheg.advancedmanhunt.event.ManHuntGameStopEvent;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import me.supcheg.advancedmanhunt.gui.api.context.GuiTickContext;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
import me.supcheg.advancedmanhunt.reflect.ReflectCalled;
import me.supcheg.advancedmanhunt.service.ManHuntGameService;
import me.supcheg.advancedmanhunt.text.GuiText;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.NAMESPACE;

public class GamesListGui implements Listener {
    public static final String KEY = NAMESPACE + ":games_list";

    private final ManHuntGameService service;
    private final ManHuntGame[] games;
    private boolean updated;

    public GamesListGui(@NotNull ManHuntGameService service) {
        this.service = service;
        this.games = new ManHuntGame[18];
        this.updated = true;

        BukkitUtil.registerEventListener(this);
    }

    @EventHandler
    public void handleCreateGame(@NotNull ManHuntGameCreateEvent event) {
        updateGameIcons();
    }

    @EventHandler
    public void handleStartGame(@NotNull ManHuntGameStartEvent event) {
        updateGameIcons();
    }

    @EventHandler
    public void handleStopGame(@NotNull ManHuntGameStopEvent event) {
        updateGameIcons();
    }

    private void updateGameIcons() {
        copyRange(service.getAllGames(), games);
        updated = true;
    }

    @SneakyThrows
    public void load(@NotNull AdvancedGuiController controller) {
        controller.loadResource(this, "gui/games_list.json");
    }

    @ReflectCalled
    private void acceptGameButtonClick(@NotNull ButtonClickContext ctx) {
        ManHuntGame game = getGameFromSlot(ctx.getSlot());

        Player player = ctx.getPlayer();
        if (service.canConfigure(player, game)) {
            game.getConfigGui().open(player);
        } else {
            player.performCommand(NAMESPACE + " game join " + game.getUniqueId());
        }
    }

    @ReflectCalled
    private void acceptGameButtonTickEnd(@NotNull ButtonTickContext ctx) {
        if (updated) {
            ManHuntGame game = getGameFromSlot(ctx.getSlot());
            AdvancedButton button = ctx.getButton();

            if (game != null) {
                button.show();
                button.setLore(buildGameInfoLore(game));
            } else {
                button.hide();
            }
        }
    }

    @Unmodifiable
    @NotNull
    @Contract("_ -> new")
    private List<Component> buildGameInfoLore(@NotNull ManHuntGame game) {
        return List.of(
                GuiText.GAMES_LIST_GAME_STATE.build(game.getState()),
                GuiText.GAMES_LIST_GAME_PLAYERS_COUNT.build(game.getPlayers().size()),
                GuiText.GAMES_LIST_GAME_OWNER.build(game.getOwner()),
                GuiText.GAMES_LIST_GAME_UNIQUE_ID.build(game.getUniqueId())
        );
    }

    @ReflectCalled
    private void acceptGuiTickEnd(@NotNull GuiTickContext ctx) {
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
