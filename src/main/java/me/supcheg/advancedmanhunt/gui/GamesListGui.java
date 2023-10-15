package me.supcheg.advancedmanhunt.gui;

import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.ManHuntGameEvent;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.IntStream;

import static me.supcheg.advancedmanhunt.gui.api.ClickActions.performCommand;

public class GamesListGui implements Listener {

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
        copyRange(repository.getManHuntGames(), games);
        updated = true;
    }

    @NotNull
    public AdvancedGui register(@NotNull AdvancedGuiController controller) {
        return controller.gui()
                .background("advancedmanhunt/games_list/background.png")
                .rows(6)
                .generateButtons(
                        IntStream.range(0, games.length),
                        index -> controller.button()
                                .slot(6 + index % 3 + index / 3 * 9)
                                .defaultShown(false)
                                .texture("advancedmanhunt/games_list/game.png")
                                .clickAction(performCommand(() -> "game join " + games[index].getUniqueId()))
                                .tick(At.TICK_END, ctx -> {
                                    if (updated) {
                                        ManHuntGame game = games[index];
                                        AdvancedButton button = ctx.getButton();

                                        if (game != null) {
                                            button.show();
                                            button.setName(Component.text(game.getUniqueId().toString()));
                                        } else {
                                            button.hide();
                                        }
                                    }
                                })
                )
                .tick(At.TICK_END, ctx -> updated = false)
                .button(controller.button()
                        .texture("advancedmanhunt/games_list/create.png")
                        .slot(10)
                        .clickAction(performCommand("game create default"))
                )
                .buildAndRegister();
    }

    private static <T> void copyRange(@NotNull Iterable<T> src, T @NotNull [] dst) {
        int index = 0;
        for (T t : src) {
            dst[index++] = t;
        }
        Arrays.fill(dst, index, dst.length, null);
    }

}
