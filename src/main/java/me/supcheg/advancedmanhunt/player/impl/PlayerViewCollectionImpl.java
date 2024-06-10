package me.supcheg.advancedmanhunt.player.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.player.PlayerViewCollection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class PlayerViewCollectionImpl implements PlayerViewCollection {

    private final Collection<UUID> uniqueIds;
    private final Collection<Player> onlinePlayers;
    private final Collection<OfflinePlayer> offlinePlayers;

    public PlayerViewCollectionImpl(@NotNull Collection<UUID> uniqueIds) {
        this.uniqueIds = uniqueIds;
        this.onlinePlayers = new UniqueIdTransformingCollection<>(Bukkit::getPlayer);
        this.offlinePlayers = new UniqueIdTransformingCollection<>(Bukkit::getOfflinePlayer);
    }

    @NotNull
    @Override
    public Collection<UUID> uniqueIds() {
        return uniqueIds;
    }

    @NotNull
    @Override
    public Collection<Player> onlinePlayers() {
        return onlinePlayers;
    }

    @NotNull
    @Override
    public Collection<OfflinePlayer> offlinePlayers() {
        return offlinePlayers;
    }

    @RequiredArgsConstructor
    private final class UniqueIdTransformingCollection<T extends OfflinePlayer> extends AbstractCollection<T> {
        private final Function<UUID, T> toPlayer;

        @Override
        public int size() {
            int count = 0;
            for (UUID uniqueId : uniqueIds) {
                if (toPlayer.apply(uniqueId) != null) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public boolean isEmpty() {
            if (uniqueIds.isEmpty()) {
                return true;
            }

            for (UUID uniqueId : uniqueIds) {
                if (toPlayer.apply(uniqueId) != null) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean contains(Object o) {
            return o == null ? uniqueIds.contains(null) : uniqueIds.contains(((OfflinePlayer) o).getUniqueId());
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new UniqueIdTransformingIterator(uniqueIds.iterator());
        }

        @Override
        public void forEach(@NotNull Consumer<? super T> action) {
            Objects.requireNonNull(action, "action");
            for (UUID uniqueId : uniqueIds) {
                T t = toPlayer.apply(uniqueId);
                if (t != null) {
                    action.accept(t);
                }
            }
        }

        @Override
        public boolean add(T player) {
            return player == null ? uniqueIds.add(null) : uniqueIds.add(player.getUniqueId());
        }

        @Override
        public boolean remove(Object o) {
            return o == null ? uniqueIds.remove(null) : uniqueIds.remove(((OfflinePlayer) o).getUniqueId());
        }

        @Override
        public void clear() {
            uniqueIds.clear();
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

        @RequiredArgsConstructor
        private final class UniqueIdTransformingIterator implements Iterator<T> {
            private final Iterator<UUID> delegate;
            private boolean valueReady;
            private T nextElement;

            @Override
            public boolean hasNext() {
                if (!valueReady) {
                    nextPlayer();
                }
                return valueReady;
            }

            private void nextPlayer() {
                while (delegate.hasNext()) {
                    T player = toPlayer.apply(delegate.next());
                    if (player != null) {
                        valueReady = true;
                        nextElement = player;
                    }
                }
            }

            @Override
            public T next() {
                if (!valueReady && !hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    valueReady = false;
                    T el = nextElement;
                    nextElement = null;
                    return el;
                }
            }

            @Override
            public void remove() {
                delegate.remove();
            }

            @Override
            public void forEachRemaining(@NotNull Consumer<? super T> action) {
                Objects.requireNonNull(action, "action");
                if (valueReady) {
                    valueReady = false;
                    T el = nextElement;
                    nextElement = null;
                    action.accept(el);
                }
                delegate.forEachRemaining(uniqueId -> {
                    T player = toPlayer.apply(uniqueId);
                    if (player != null) {
                        action.accept(player);
                    }
                });
            }
        }
    }
}
