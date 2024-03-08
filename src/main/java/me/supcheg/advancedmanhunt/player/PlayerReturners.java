package me.supcheg.advancedmanhunt.player;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.player.impl.EventInitializingPlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.TeleportingPlayerReturner;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerReturners {
    @NotNull
    @Contract("-> new")
    public static PlayerReturner loadPlayerReturner() {
        String returnerType = config().game.playerReturner.type;
        String returnerArgument = config().game.playerReturner.argument;

        return switch (returnerType.toLowerCase()) {
            case "teleport", "tp", "teleporting" -> new TeleportingPlayerReturner(returnerArgument);
            case "custom", "event" -> new EventInitializingPlayerReturner();
            default -> throw new IllegalArgumentException(returnerType);
        };
    }
}
