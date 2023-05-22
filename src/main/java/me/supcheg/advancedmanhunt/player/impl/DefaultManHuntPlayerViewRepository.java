package me.supcheg.advancedmanhunt.player.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerViewRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DefaultManHuntPlayerViewRepository implements ManHuntPlayerViewRepository {

    private final Cache<UUID, ManHuntPlayerView> uniqueId2view = CacheBuilder.newBuilder()
            .weakValues()
            .expireAfterWrite(20, TimeUnit.MINUTES)
            .expireAfterAccess(20, TimeUnit.MINUTES)
            .build();
    private final Collection<ManHuntPlayerView> allViews = Collections.unmodifiableCollection(uniqueId2view.asMap().values());

    @Override
    @NotNull
    @UnmodifiableView
    public Collection<ManHuntPlayerView> getPlayers() {
        return allViews;
    }

    @SneakyThrows
    @Override
    @NotNull
    public ManHuntPlayerView get(@NotNull UUID uniqueId) {
        return uniqueId2view.get(uniqueId, () -> new DefaultManHuntPlayerView(uniqueId));
    }
}
