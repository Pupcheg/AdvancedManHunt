package me.supcheg.advancedmanhunt.structure;

import me.supcheg.advancedmanhunt.storage.InMemoryEntityRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DynamicRepository<E, K> extends InMemoryEntityRepository<E, K> {
    private final Function<K, E> key2entity;

    public DynamicRepository(@NotNull Function<E, K> entity2key, @NotNull Function<K, E> key2entity) {
        super(entity2key);
        this.key2entity = key2entity;
    }

    @Nullable
    @Override
    public E getEntity(@NotNull K key) {
        E entity = key2entity.apply(key);
        storeEntity(entity);
        return entity;
    }
}
