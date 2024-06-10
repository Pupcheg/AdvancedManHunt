package me.supcheg.advancedmanhunt.region;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.region.impl.ThriftyGameRegionRepository;

import javax.inject.Singleton;

@Module
public interface GameRegionModule {
    @Binds
    @Singleton
    GameRegionRepository gameRegionRepository(ThriftyGameRegionRepository repo);
}
