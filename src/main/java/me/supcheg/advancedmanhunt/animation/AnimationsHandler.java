package me.supcheg.advancedmanhunt.animation;

import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.event.ManHuntGameStartEvent;
import me.supcheg.advancedmanhunt.event.ManHuntGameStopEvent;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
public class AnimationsHandler implements Listener {
    private static final String GAME_START = "game_start";
    private static final String GAME_STOP = "game_stop";

    private final PlayerAnimationsRepository playerAnimationsRepository;

    @EventHandler
    public void handleManHuntGameStart(@NotNull ManHuntGameStartEvent event) {
        playSelectedAnimations(GAME_START, event.getManHuntGame().getPlayers());
    }

    @EventHandler
    public void handleManHuntGameStop(@NotNull ManHuntGameStopEvent event) {
        playSelectedAnimations(GAME_STOP, event.getManHuntGame().getPlayers());
    }

    private void playSelectedAnimations(@NotNull String object, @NotNull Iterable<ManHuntPlayerView> playerViews) {
        for (ManHuntPlayerView playerView : playerViews) {
            playSelectedAnimation(object, playerView);
        }
    }

    private void playSelectedAnimation(@NotNull String object, @NotNull ManHuntPlayerView playerView) {
        UUID uniqueId = playerView.getUniqueId();
        Player player = playerView.getPlayer();
        if (player != null) {
            Animation animation = playerAnimationsRepository.getSelectedAnimation(uniqueId, object);
            if (animation != null) {
                animation.play(player.getLocation());
            }
        }
    }
}
