package me.supcheg.advancedmanhunt.storage;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.reflect.Types;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class PathSerializingEntityRepository<E, K> extends InMemoryEntityRepository<E, K> implements EntityRepository<E, K> {

    protected final Type entitiesListType;
    protected final Path path;
    protected final Gson gson;

    protected PathSerializingEntityRepository(@NotNull Path path, @NotNull Gson gson,
                                              @NotNull Type entitiesType, @NotNull Function<E, K> entity2key) {
        super(entity2key);
        this.path = path;
        this.gson = gson;
        this.entitiesListType = Types.type(List.class, entitiesType);

        load();
    }

    @NotNull
    public Gson getGson() {
        return gson;
    }

    @NotNull
    public Path getPath() {
        return path;
    }

    @SneakyThrows
    public void load() {
        if (Files.notExists(path)) {
            return;
        }

        List<E> loaded;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            loaded = gson.fromJson(reader, entitiesListType);
        }
        if (loaded != null) {
            loaded.forEach(this::storeEntity);
        }
    }

    @Override
    @SneakyThrows
    public void save() {
        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(entities.values(), writer);
        }
    }
}
