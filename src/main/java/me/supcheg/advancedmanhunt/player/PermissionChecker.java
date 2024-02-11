package me.supcheg.advancedmanhunt.player;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionChecker {
    public static boolean canConfigure(@NotNull Player player, @NotNull ManHuntGame game) {
        return game.getOwner().equals(player.getUniqueId()) || player.hasPermission(Permission.CONFIGURE_ANY_GAME);
    }
}
