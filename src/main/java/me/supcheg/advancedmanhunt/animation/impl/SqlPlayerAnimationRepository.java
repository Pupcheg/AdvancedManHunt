package me.supcheg.advancedmanhunt.animation.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.animation.Animation;
import me.supcheg.advancedmanhunt.animation.AnimationConfiguration;
import me.supcheg.advancedmanhunt.animation.AnimationRepository;
import me.supcheg.advancedmanhunt.animation.PlayerAnimationsRepository;
import me.supcheg.advancedmanhunt.json.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SqlPlayerAnimationRepository implements PlayerAnimationsRepository, AutoCloseable {
    private static final Type STRING_STRING_MAP = Types.type(Map.class, String.class, String.class);
    private static final Type STRING_STRING_STRING_MAP_MAP = Types.type(Map.class, String.class, STRING_STRING_MAP);
    private static final Type STRING_LIST = Types.type(List.class, String.class);
    private final Connection connection;
    private final AnimationRepository animationRepository;
    private final Gson gson;
    private final Cache<UUID, AnimationUser> usersCache;

    @SneakyThrows
    public SqlPlayerAnimationRepository(@NotNull AnimationRepository animationRepository, @NotNull Gson gson) {
        this.connection = DriverManager.getConnection("jdbc::memory:");
        this.animationRepository = animationRepository;
        this.gson = gson;
        this.usersCache = CacheBuilder.newBuilder()
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .build();
    }

    @Nullable
    @Override
    public Animation getAnimation(@NotNull UUID uniqueId, @NotNull String object) {
        String selected = getUser(uniqueId).getObjectToSelectedAnimation().get(object);
        return selected == null ? null : animationRepository.getAnimation(selected);
    }

    @Override
    public void setAnimation(@NotNull UUID uniqueId, @NotNull String object, @NotNull Animation animation) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public AnimationConfiguration getAnimationConfiguration(@NotNull UUID uniqueId, @NotNull Animation animation) {
        Map<String, String> setting2value = getUser(uniqueId).getAnimationToConfiguration().get(animation.getKey());

        return setting2value == null || setting2value.isEmpty() ?
                EmptyAnimationConfiguration.INSTANCE
                : new MapBasedAnimationConfiguration(setting2value);
    }

    @Override
    public void setAnimationConfiguration(@NotNull UUID uniqueId, @NotNull String object, @NotNull AnimationConfiguration configuration) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Collection<Animation> getAvailableAnimations(@NotNull UUID uniqueId) {
        return Collections2.transform(getUser(uniqueId).getAvailableAnimations(), animationRepository::getAnimation);
    }

    @SneakyThrows
    @NotNull
    private AnimationUser getUser(@NotNull UUID uniqueId) {
        return usersCache.get(uniqueId, () -> {
            String rawSelected;
            String rawConfiguration;
            String rawAvailable;
            boolean insert;

            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM animations_users WHERE uuid=? LIMIT 1")) {
                ps.setString(1, uniqueId.toString());
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    rawSelected = rs.getString("selected");
                    rawConfiguration = rs.getString("configuration");
                    rawAvailable = rs.getString("available");
                    insert = false;
                } else {
                    rawSelected = "{}";
                    rawConfiguration = "{}";
                    rawAvailable = "[]";
                    insert = true;
                }
            }

            if (insert) {
                try (PreparedStatement ps = connection.prepareStatement("INSERT INTO animations_users (?,?,?,?)")) {
                    ps.setString(1, uniqueId.toString());
                    ps.setString(2, rawSelected);
                    ps.setString(3, rawConfiguration);
                    ps.setString(4, rawAvailable);
                    ps.execute();
                }
            }

            Map<String, String> selected = gson.fromJson(rawSelected, STRING_STRING_MAP);
            Map<String, Map<String, String>> configuration = gson.fromJson(rawConfiguration, STRING_STRING_STRING_MAP_MAP);
            List<String> available = gson.fromJson(rawAvailable, STRING_LIST);

            return new AnimationUser(uniqueId, selected, configuration, available);
        });
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    private static final class AnimationUser {
        private final UUID uniqueId;
        private Map<String, String> objectToSelectedAnimation;
        private Map<String, Map<String, String>> animationToConfiguration;
        private List<String> availableAnimations;
    }
}
