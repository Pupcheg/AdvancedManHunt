package me.supcheg.advancedmanhunt.player;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

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
    public static boolean isOffline(@NotNull UUID uniqueId) {
        return Bukkit.getPlayer(uniqueId) == null;
    }

    @Contract(pure = true)
    public static boolean isNonNullAndOnline(@Nullable UUID uniqueId) {
        return uniqueId != null && Bukkit.getPlayer(uniqueId) != null;
    }

    @Contract(pure = true)
    public static boolean areAllOnline(@NotNull Collection<UUID> uniqueIds) {
        if (uniqueIds.isEmpty()) {
            return true;
        }

        for (UUID uniqueId : uniqueIds) {
            if (isOffline(uniqueId)) {
                return false;
            }
        }
        return true;
    }

    @Contract(pure = true)
    public static boolean areAllOffline(@NotNull Collection<UUID> uniqueIds) {
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
    public static Collection<Player> asPlayersView(@NotNull Collection<UUID> uniqueIds) {
        return new AsPlayersCollection(Objects.requireNonNull(uniqueIds, "uniqueIds"));
    }

    @RequiredArgsConstructor
    private static final class AsPlayersCollection extends AbstractCollection<Player> {
        private final Collection<UUID> delegate;

        @Override
        public int size() {
            return countOnlinePlayers(delegate);
        }

        @Override
        public boolean isEmpty() {
            return areAllOffline(delegate);
        }

        @Override
        public boolean contains(Object o) {
            return o == null ? delegate.contains(null) : delegate.contains(((Player) o).getUniqueId());
        }

        @NotNull
        @Override
        public Iterator<Player> iterator() {
            return asPlayerIterator(delegate.iterator());
        }

        @Override
        public void forEach(@NotNull Consumer<? super Player> action) {
            Objects.requireNonNull(action, "action");
            Players.forEach(delegate, uncheckedCast(action));
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

        @Override
        public String toString() {
            return delegate.toString();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof Collection<?> other)) {
                return false;
            }
            return this.size() == other.size() && this.containsAll(other);
        }
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Iterator<Player> asPlayerIterator(@NotNull Iterator<UUID> uniqueIds) {
        return new AsPlayerIterator(Objects.requireNonNull(uniqueIds, "uniqueIds"));
    }

    @RequiredArgsConstructor
    private static final class AsPlayerIterator implements Iterator<Player> {
        private final Iterator<UUID> delegate;
        private boolean valueReady;
        private Player nextElement;

        @Override
        public boolean hasNext() {
            if (!valueReady) {
                nextPlayer();
            }
            return valueReady;
        }

        private void nextPlayer() {
            while (delegate.hasNext()) {
                Player player = Bukkit.getPlayer(delegate.next());
                if (player != null) {
                    valueReady = true;
                    nextElement = player;
                }
            }
        }

        @Override
        public Player next() {
            if (!valueReady && !hasNext()) {
                throw new NoSuchElementException();
            } else {
                valueReady = false;
                Player el = nextElement;
                nextElement = null;
                return el;
            }
        }

        @Override
        public void remove() {
            delegate.remove();
        }

        @Override
        public void forEachRemaining(@NotNull Consumer<? super Player> action) {
            Objects.requireNonNull(action, "action");
            if (valueReady) {
                valueReady = false;
                Player el = nextElement;
                nextElement = null;
                action.accept(el);
            }
            delegate.forEachRemaining(uniqueId -> {
                Player player = Bukkit.getPlayer(uniqueId);
                if (player != null) {
                    action.accept(player);
                }
            });
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
