package me.supcheg.advancedmanhunt.gui;

import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.ManHuntGameEvent;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.text.GuiText;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.IntStream;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.NAMESPACE;
import static me.supcheg.advancedmanhunt.gui.api.ClickActions.performCommand;

public class GamesListGui implements Listener {
    public static final String KEY = NAMESPACE + ":games_list";

    private final ManHuntGameRepository repository;
    private final ManHuntGame[] games;
    private boolean updated;

    public GamesListGui(@NotNull ManHuntGameRepository repository, @NotNull EventListenerRegistry registry) {
        this.repository = repository;
        this.games = new ManHuntGame[3 * 6];
        this.updated = true;

        registry.addListener(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onManHuntGameEvent(@NotNull ManHuntGameEvent event) {
        copyRange(repository.getEntities(), games);
        updated = true;
    }

    public void register(@NotNull AdvancedGuiController controller) {
        controller.gui()
                .key(KEY)
                .background(NAMESPACE + "/games_list/background.png")
                .rows(6)
                .generateButtons(
                        IntStream.range(0, games.length),
                        index -> controller.button()
                                .slot(6 + index % 3 + index / 3 * 9)
                                .defaultShown(false)
                                .name(GuiText.GAMES_LIST_GAME_NAME.build())
                                .texture(NAMESPACE + "/games_list/game.png")
                                .clickAction(performCommand(() -> NAMESPACE + " game join " + games[index].getUniqueId()))
                                .tick(At.TICK_END, ctx -> {
                                    if (updated) {
                                        ManHuntGame game = games[index];
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
                                })
                )
                .tick(At.TICK_END, ctx -> updated = false)
                .button(controller.button()
                        .texture(NAMESPACE + "/games_list/create.png")
                        .name(GuiText.GAMES_LIST_CREATE_NAME.build())
                        .lore(GuiText.GAMES_LIST_CREATE_LORE.build())
                        .slot(10)
                        .clickAction(performCommand(NAMESPACE + " game create"))
                )
                .buildAndRegister();
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
