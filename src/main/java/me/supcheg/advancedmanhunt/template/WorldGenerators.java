package me.supcheg.advancedmanhunt.template;

import dagger.Module;
import dagger.Provides;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.platform.paper.PluginUtil;
import me.supcheg.advancedmanhunt.template.impl.BukkitWorldGenerator;
import me.supcheg.advancedmanhunt.template.impl.ChunkyWorldGenerator;

import javax.inject.Singleton;

@Module
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorldGenerators {
    @Provides
    @Singleton
    public static WorldGenerator loadWorldGenerator() {
        return PluginUtil.isPluginInstalled("Chunky") ? new ChunkyWorldGenerator() : new BukkitWorldGenerator();
    }
}
