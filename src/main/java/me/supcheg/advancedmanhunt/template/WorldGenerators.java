package me.supcheg.advancedmanhunt.template;

import dagger.Module;
import dagger.Provides;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
import me.supcheg.advancedmanhunt.template.impl.BukkitWorldGenerator;
import me.supcheg.advancedmanhunt.template.impl.ChunkyWorldGenerator;

import javax.inject.Singleton;

@Module
@CustomLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorldGenerators {
    @Provides
    @Singleton
    public static WorldGenerator loadWorldGenerator() {
        return BukkitUtil.isPluginInstalled("Chunky") ? new ChunkyWorldGenerator() : new BukkitWorldGenerator();
    }
}
