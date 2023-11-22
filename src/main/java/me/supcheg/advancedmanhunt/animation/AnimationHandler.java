package me.supcheg.advancedmanhunt.animation;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.event.ManHuntGameStartEvent;
import me.supcheg.advancedmanhunt.event.ManHuntGameStopEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class AnimationHandler implements Listener {
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

    private void playSelectedAnimations(@NotNull String object, @NotNull Iterable<UUID> uniqueIds) {
        for (UUID uniqueId : uniqueIds) {
            playSelectedAnimation(object, uniqueId);
        }
    }

    private void playSelectedAnimation(@NotNull String object, @NotNull UUID uniqueId) {
        Player player = Bukkit.getPlayer(uniqueId);
        if (player != null) {
            Animation animation = playerAnimationsRepository.getSelectedAnimation(uniqueId, object);
            if (animation != null) {
                animation.play(player.getLocation());
            }
        }
    }
}
