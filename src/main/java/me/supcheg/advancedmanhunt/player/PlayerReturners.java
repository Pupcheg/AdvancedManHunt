package me.supcheg.advancedmanhunt.player;

import dagger.Module;
import dagger.Provides;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.player.impl.EventInitializingPlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.LoggingPlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.TeleportingPlayerReturner;

import javax.inject.Singleton;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@Module
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerReturners {
    @Provides
    @Singleton
    public static PlayerReturner loadPlayerReturner() {
        String returnerType = config().game.playerReturner.type;
        String returnerArgument = config().game.playerReturner.argument;

        PlayerReturner playerReturner = switch (returnerType.toLowerCase()) {
            case "teleport", "tp", "teleporting" -> new TeleportingPlayerReturner(returnerArgument);
            case "custom", "event" -> new EventInitializingPlayerReturner();
            default -> throw new IllegalArgumentException(returnerType);
        };

        if (config().debug) {
            playerReturner = new LoggingPlayerReturner(playerReturner);
        }

        return playerReturner;
    }
}
