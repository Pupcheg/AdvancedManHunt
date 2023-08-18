package me.supcheg.advancedmanhunt.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.errorprone.annotations.MustBeClosed;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

public class HikariEntityRepository<E, K> implements GsonEntityRepository<E, K> {
    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("table_name", Pattern.LITERAL);

    private volatile boolean isTableInitialized;
    private final HikariDataSource dataSource;
    private final Configuration<K> configuration;
    private final Cache<K, E> entitiesCache;
    private final Function<E, K> entity2key;

    public HikariEntityRepository(@NotNull HikariConfig hikariConfig,
                                  @NotNull Configuration<K> configuration,
                                  @NotNull Function<E, K> entity2key) {
        this.dataSource = new HikariDataSource(hikariConfig);
        this.configuration = configuration;
        this.entity2key = entity2key;
        this.entitiesCache = CacheBuilder.newBuilder()
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public boolean storeEntity(@NotNull E entity) {
        entitiesCache.put(entity2key.apply(entity), entity);
        saveEntityToDatabase(entity);
        return true;
    }

    @Override
    public boolean invalidateKey(@NotNull K key) {
        entitiesCache.invalidate(key);
        return removeEntityFromDatabase(key);
    }

    @SneakyThrows
    @Override
    public boolean containsKey(@NotNull K key) {
        return isCached(key) || loadEntityToCacheIfPresentInDatabase(key) != null;
    }

    @SneakyThrows
    @Nullable
    @Override
    public E getEntity(@NotNull K key) {
        return entitiesCache.get(key, () -> loadEntityToCacheIfPresentInDatabase(key));
    }

    @NotNull
    @Override
    public E getOrCreateEntity(@NotNull K key, @NotNull Function<K, E> function) {
        E entity = getEntity(key);
        if (entity == null) {
            entity = function.apply(key);
            saveEntityToDatabase(entity);
            entitiesCache.put(key, entity);
        }
        return entity;
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Map<K, E> getKeyToEntity() {
        return entitiesCache.asMap();
    }

    @NotNull
    @Override
    public K getKey(@NotNull E entity) {
        return entity2key.apply(entity);
    }

    private boolean isCached(@NotNull K key) {
        return entitiesCache.getIfPresent(key) != null;
    }

    @Nullable
    private E loadEntityToCacheIfPresentInDatabase(@NotNull K key) {
        // TODO: 18.08.2023
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    private void saveEntityToDatabase(@NotNull E entity) {
        // TODO: 18.08.2023
    }

    @SneakyThrows
    private boolean removeEntityFromDatabase(K key) {
        try (PreparedStatement ps = prepareStatement("DELETE FROM table_name WHERE key=? LIMIT 1")) {
            ps.setString(1, getSqlKey(key));
            return ps.executeUpdate() > 0;
        }
    }

    private String getSqlKey(K key) {
        return configuration.getKeyToString().apply(key);
    }

    private void ensureTableInitialized(E entity) {
        if (!isTableInitialized) {
            synchronized (this) {
                if (!isTableInitialized) {
                    initializeTable(entity);
                    isTableInitialized = true;
                }
            }
        }
    }

    @SneakyThrows
    private void initializeTable(@NotNull E entity) {
        // TODO: 18.08.2023
    }

    @MustBeClosed
    @NotNull
    private PreparedStatement prepareStatement(@NotNull String sql) throws SQLException {
        return dataSource.getConnection().prepareStatement(replaceTableName(sql));
    }

    private String replaceTableName(String sql) {
        return TABLE_NAME_PATTERN.matcher(sql).replaceFirst(configuration.getTableName());
    }

    @Override
    public void close() {
        dataSource.close();
    }

    @Builder
    @Data
    public static final class Configuration<K> {
        private final String tableName;
        private final String keyName;
        private final Function<K, String> keyToString;
        private final Gson gson;
    }
}
