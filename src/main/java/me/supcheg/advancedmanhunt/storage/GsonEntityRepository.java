package me.supcheg.advancedmanhunt.storage;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public interface GsonEntityRepository<E, K> extends EntityRepository<E, K> {
    @NotNull
    Gson getGson();
}
