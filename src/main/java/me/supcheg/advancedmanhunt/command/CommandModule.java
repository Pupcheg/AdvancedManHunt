package me.supcheg.advancedmanhunt.command;

import dagger.Binds;
import dagger.Module;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public interface CommandModule {
    @Binds
    @Singleton
    @Named("advancedmanhunt")
    BukkitBrigadierCommand advancedmanhuntCommand(AdvancedManHuntCommand command);

    @Binds
    @Singleton
    @Named("debug")
    BukkitBrigadierCommand debugCommand(DebugCommand command);

    @Binds
    @Singleton
    @Named("game")
    BukkitBrigadierCommand gameCommand(GameCommand command);

    @Binds
    @Singleton
    @Named("template")
    BukkitBrigadierCommand templateCommand(TemplateCommand command);
}
