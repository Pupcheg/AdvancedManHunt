package me.supcheg.advancedmanhunt.storage;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Repositories {

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static <E, K> GsonEntityRepository<E, K> pathSerializing(@NotNull Path path, @NotNull Gson gson,
                                                                    @NotNull Type entitiesType, @NotNull Function<E, K> entity2key) {
        return new PathSerializingEntityRepository<>(path, gson, entitiesType, entity2key);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <E, K> EntityRepository<E, K> inMemory(@NotNull Function<E, K> entity2key) {
        return new InMemoryEntityRepository<>(entity2key);
    }
}
