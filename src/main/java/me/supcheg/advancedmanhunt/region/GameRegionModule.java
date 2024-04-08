package me.supcheg.advancedmanhunt.region;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;

import javax.inject.Singleton;

@Module
public interface GameRegionModule {
    @Binds
    @Singleton
    GameRegionRepository gameRegionRepository(DefaultGameRegionRepository repo);
}
