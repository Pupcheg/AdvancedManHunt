package me.supcheg.advancedmanhunt.player;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.player.impl.DefaultPlayerFreezer;

import javax.inject.Singleton;

@Module
public interface PlayerModule {
    @Binds
    @Singleton
    PlayerFreezer playerFreezer(DefaultPlayerFreezer freezer);
}
