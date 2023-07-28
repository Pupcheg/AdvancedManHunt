package me.supcheg.advancedmanhunt.animation.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.supcheg.advancedmanhunt.animation.Animation;
import me.supcheg.advancedmanhunt.animation.AnimationRepository;
import me.supcheg.advancedmanhunt.animation.PlayerAnimationsRepository;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.collect.Collections2.transform;
import static java.util.Collections.unmodifiableCollection;

public class DefaultPlayerAnimationRepository implements PlayerAnimationsRepository, AutoCloseable {
    private static final CustomLogger LOGGER = CustomLogger.getLogger(DefaultPlayerAnimationRepository.class);

    private static final String ANIMATIONS_USERS_FILE = "animations_users.json";

    private static final Type STRING_TO_STRING_MAP = Types.type(Map.class, String.class, String.class);
    private static final Type STRING_LIST = Types.type(List.class, String.class);
    private static final Type USERS_LIST = Types.type(List.class, AnimationUser.class);

    private final AnimationRepository animationRepository;
    private final Path filePath;
    private final Gson gson;
    private final Map<UUID, AnimationUser> users;

    public DefaultPlayerAnimationRepository(@NotNull AnimationRepository animationRepository,
                                            @NotNull ContainerAdapter containerAdapter) {
        this.animationRepository = animationRepository;
        this.filePath = containerAdapter.resolveData(ANIMATIONS_USERS_FILE);
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(new TypeAdapterFactory() {
                    @Nullable
                    @Override
                    @SuppressWarnings("unchecked")
                    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
                        return type.getRawType() == AnimationUser.class ?
                                (TypeAdapter<T>) new AnimationUserTypeAdapter(gson).nullSafe() : null;
                    }
                })
                .create();
        this.users = new HashMap<>();

        loadCachedUsers();
    }

    private void loadCachedUsers() {
        List<AnimationUser> cachedUsers = Collections.emptyList();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            cachedUsers = gson.fromJson(reader, USERS_LIST);
        } catch (IOException e) {
            LOGGER.error("Unable to load {}", filePath, e);
        }

        for (AnimationUser user : cachedUsers) {
            this.users.put(user.getUniqueId(), user);
        }
    }

    @Nullable
    @Override
    public Animation getSelectedAnimation(@NotNull UUID uniqueId, @NotNull String object) {
        String selected = getUser(uniqueId).getObjectToSelectedAnimation().get(object);
        return selected == null ? null : animationRepository.getAnimation(selected);
    }

    @Override
    public void setSelectedAnimation(@NotNull UUID uniqueId, @NotNull String object, @NotNull Animation animation) {
        getUser(uniqueId).getObjectToSelectedAnimation().put(object, animation.getKey());
    }

    @NotNull
    @Override
    public Collection<Animation> getAvailableAnimations(@NotNull UUID uniqueId) {
        return unmodifiableCollection(transform(getUser(uniqueId).getAvailableAnimations(), animationRepository::getAnimation));
    }

    @Override
    public void addAvailableAnimation(@NotNull UUID uniqueId, @NotNull Animation animation) {
        getUser(uniqueId).getAvailableAnimations()
                .add(animation.getKey());
    }

    @Override
    public void removeAvailableAnimation(@NotNull UUID uniqueId, @NotNull Animation animation) {
        getUser(uniqueId).getAvailableAnimations()
                .remove(animation.getKey());
    }

    private AnimationUser getUser(@NotNull UUID uniqueID) {
        return users.computeIfAbsent(uniqueID, AnimationUser::new);
    }

    @Override
    public void close() throws Exception {
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(new ArrayList<>(users.values()), USERS_LIST, writer);
        }
    }

    @Getter
    @AllArgsConstructor
    private static final class AnimationUser {
        private final UUID uniqueId;
        private Map<String, String> objectToSelectedAnimation;
        private List<String> availableAnimations;

        public AnimationUser(@NotNull UUID uniqueId) {
            this.uniqueId = uniqueId;
            this.objectToSelectedAnimation = new HashMap<>();
            availableAnimations = new ArrayList<>();
        }
    }

    @AllArgsConstructor
    private static final class AnimationUserTypeAdapter extends TypeAdapter<AnimationUser> {
        private static final String UUID = "uuid";
        private static final String SELECTED = "selected";
        private static final String AVAILABLE = "available";

        private final Gson gson;

        @Override
        public void write(@NotNull JsonWriter out, @NotNull AnimationUser value) throws IOException {
            out.beginObject();

            out.name(UUID);
            out.value(value.uniqueId.toString());

            out.name(SELECTED);
            gson.toJson(value.objectToSelectedAnimation, STRING_TO_STRING_MAP, out);

            out.name(AVAILABLE);
            gson.toJson(value.availableAnimations, STRING_LIST, out);

            out.endObject();
        }

        @NotNull
        @Override
        public AnimationUser read(@NotNull JsonReader in) throws IOException {
            in.beginObject();

            UUID uniqueId = null;
            Map<String, String> selected = null;
            List<String> available = null;

            while (in.hasNext()) {
                switch (in.nextName()) {
                    case UUID -> uniqueId = java.util.UUID.fromString(in.nextString());
                    case SELECTED -> selected = gson.fromJson(in, STRING_TO_STRING_MAP);
                    case AVAILABLE -> available = gson.fromJson(in, STRING_LIST);
                }
            }

            Objects.requireNonNull(uniqueId);
            Objects.requireNonNull(selected);
            Objects.requireNonNull(available);

            in.endObject();

            return new AnimationUser(uniqueId, selected, available);
        }
    }
}
