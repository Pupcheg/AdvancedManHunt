package me.supcheg.advancedmanhunt.player;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Players {

    @NotNull
    public static Player getPlayer(@NotNull UUID uniqueId) {
        return Objects.requireNonNull(Bukkit.getPlayer(uniqueId), "Player with id=" + uniqueId);
    }

    @Contract(pure = true)
    public static boolean isOnline(@NotNull UUID uniqueId) {
        return Bukkit.getPlayer(uniqueId) != null;
    }

    @Contract(pure = true)
    public static boolean isNotNullAndOnline(@Nullable UUID uniqueId) {
        return uniqueId != null && Bukkit.getPlayer(uniqueId) != null;
    }

    @Contract(pure = true)
    public static boolean isAllOnline(@NotNull Collection<UUID> uniqueIds) {
        if (uniqueIds.isEmpty()) {
            return true;
        }

        for (UUID uniqueId : uniqueIds) {
            if (!isOnline(uniqueId)) {
                return false;
            }
        }
        return true;
    }

    @Contract(pure = true)
    public static boolean isAnyOnline(@NotNull Collection<UUID> uniqueIds) {
        if (uniqueIds.isEmpty()) {
            return false;
        }

        for (UUID uniqueId : uniqueIds) {
            if (isOnline(uniqueId)) {
                return true;
            }
        }
        return false;
    }

    @Contract(pure = true)
    public static boolean isNoneOnline(@NotNull Collection<UUID> uniqueIds) {
        if (uniqueIds.isEmpty()) {
            return true;
        }

        for (UUID uniqueId : uniqueIds) {
            if (isOnline(uniqueId)) {
                return false;
            }
        }
        return true;
    }

    @Contract(pure = true)
    public static int countOnlinePlayers(@NotNull Iterable<UUID> uniqueIds) {
        int count = 0;
        for (UUID uniqueId : uniqueIds) {
            if (isOnline(uniqueId)) {
                count++;
            }
        }
        return count;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static List<Player> asPlayersList(@NotNull Collection<UUID> uniqueIds) {
        List<Player> collection = new ArrayList<>(uniqueIds.size());
        for (UUID uniqueId : uniqueIds) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player != null) {
                collection.add(player);
            }
        }
        return collection;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Collection<Player> asPlayersView(@NotNull Collection<UUID> uniqueIds) {
        return new AsPlayersCollection(Objects.requireNonNull(uniqueIds, "uniqueIds"));
    }

    @RequiredArgsConstructor
    private static class AsPlayersCollection extends AbstractCollection<Player> {
        private final Collection<UUID> delegate;

        @Override
        public int size() {
            return countOnlinePlayers(delegate);
        }

        @Override
        public boolean isEmpty() {
            return isNoneOnline(delegate);
        }

        @Override
        public boolean contains(Object o) {
            return o == null ? delegate.contains(null) : delegate.contains(((Player) o).getUniqueId());
        }

        @NotNull
        @Override
        public Iterator<Player> iterator() {
            return delegate.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).iterator();
        }

        @Override
        public boolean add(Player player) {
            return player == null ? delegate.add(null) : delegate.add(player.getUniqueId());
        }

        @Override
        public boolean remove(Object o) {
            return o == null ? delegate.remove(null) : delegate.remove(((Player) o).getUniqueId());
        }

        @Override
        public void clear() {
            delegate.clear();
        }
    }

    @Contract(pure = true)
    public static void forEach(@NotNull Collection<UUID> uniqueIds, @NotNull Consumer<Player> consumer) {
        for (UUID uniqueId : uniqueIds) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player != null) {
                consumer.accept(player);
            }
        }
    }

}
