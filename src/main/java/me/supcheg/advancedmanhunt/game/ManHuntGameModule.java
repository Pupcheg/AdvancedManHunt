package me.supcheg.advancedmanhunt.game;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.game.gui.ManHuntGamesListGui;
import me.supcheg.advancedmanhunt.game.impl.InMemoryManHuntGameRepository;

import javax.inject.Singleton;

@Module
public interface ManHuntGameModule {
    @Binds
    @Singleton
    ManHuntGameRepository manhuntGameRepository(InMemoryManHuntGameRepository repo);

    @Binds
    @Singleton
    Object manhuntGameService(ManHuntGameService service);

    @Binds
    @Singleton
    Object manhuntGamesListGui(ManHuntGamesListGui gui);
}
