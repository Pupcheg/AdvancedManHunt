package me.supcheg.advancedmanhunt.game;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;

import javax.inject.Singleton;

@Module
public interface ManHuntGameModule {
    @Binds
    @Singleton
    ManHuntGameRepository manhuntGameRepository(DefaultManHuntGameRepository repo);

    @Binds
    @Singleton
    Object manhuntGameService(ManHuntGameService service);
}
