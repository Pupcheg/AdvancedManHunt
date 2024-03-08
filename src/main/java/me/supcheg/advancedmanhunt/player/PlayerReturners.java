package me.supcheg.advancedmanhunt.player;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.player.impl.EventInitializingPlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.TeleportingPlayerReturner;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerReturners {
    @NotNull
    @Contract("-> new")
    public static PlayerReturner loadPlayerReturner() {
        String returnerType = AdvancedManHuntConfig.get().game.playerReturner.type;
        String returnerArgument = AdvancedManHuntConfig.get().game.playerReturner.argument;

        return switch (returnerType.toLowerCase()) {
            case "teleport", "tp", "teleporting" -> new TeleportingPlayerReturner(returnerArgument);
            case "custom", "event" -> new EventInitializingPlayerReturner();
            default -> throw new IllegalArgumentException(returnerType);
        };
    }
}
