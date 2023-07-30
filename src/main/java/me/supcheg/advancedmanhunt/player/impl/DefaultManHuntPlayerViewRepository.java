package me.supcheg.advancedmanhunt.player.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerViewRepository;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DefaultManHuntPlayerViewRepository implements ManHuntPlayerViewRepository {
    private final Cache<UUID, ManHuntPlayerView> uniqueId2view = CacheBuilder.newBuilder().weakValues().build();

    @SneakyThrows
    @NotNull
    @Override
    public ManHuntPlayerView get(@NotNull UUID uniqueId) {
        return uniqueId2view.get(uniqueId, () -> new DefaultManHuntPlayerView(uniqueId));
    }
}
