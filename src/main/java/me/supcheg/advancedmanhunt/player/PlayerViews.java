package me.supcheg.advancedmanhunt.player;

import com.google.common.collect.Sets;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class PlayerViews {

    @Contract(pure = true)
    public static boolean nonNullAndOnline(@Nullable ManHuntPlayerView playerView) {
        return playerView != null && playerView.isOnline();
    }

    @Contract(pure = true)
    public static boolean isAllOnline(@NotNull Collection<ManHuntPlayerView> playerViews) {
        for (ManHuntPlayerView playerView : playerViews) {
            if (!playerView.isOnline()) {
                return false;
            }
        }
        return true;
    }

    @Contract(pure = true)
    public static boolean isAnyOnline(@NotNull Collection<ManHuntPlayerView> playerViews) {
        if (playerViews.isEmpty()) {
            return false;
        }

        for (ManHuntPlayerView playerView : playerViews) {
            if (playerView.isOnline()) {
                return true;
            }
        }
        return false;
    }

    @Contract(pure = true)
    public static boolean isNoneOnline(@NotNull Collection<ManHuntPlayerView> playerViews) {
        for (ManHuntPlayerView playerView : playerViews) {
            if (playerView.isOnline()) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static Set<Player> asPlayersSet(@NotNull Collection<ManHuntPlayerView> playerViews, boolean containNull) {
        return asPlayersCollection(playerViews, Sets::newHashSetWithExpectedSize, containNull);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static List<Player> asPlayersList(@NotNull Collection<ManHuntPlayerView> playerViews, boolean containNulls) {
        return asPlayersCollection(playerViews, ArrayList::new, containNulls);
    }


    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static <T extends Collection<Player>> T asPlayersCollection(@NotNull Collection<ManHuntPlayerView> playerViews,
                                                                       @NotNull IntFunction<T> generator, boolean containNulls) {
        T collection = generator.apply(playerViews.size());
        for (ManHuntPlayerView playerView : playerViews) {
            Player player = playerView.getPlayer();
            if (containNulls || player != null) {
                collection.add(player);
            }
        }
        return collection;
    }

    @Contract(pure = true)
    public static void forEach(@NotNull Collection<ManHuntPlayerView> playerViews, @NotNull Consumer<Player> consumer) {
        for (ManHuntPlayerView playerView : playerViews) {
            Player player = playerView.getPlayer();
            if (player != null) {
                consumer.accept(player);
            }
        }
    }

}
