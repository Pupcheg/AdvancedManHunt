package me.supcheg.advancedmanhunt.command;

import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public interface CommandModule {
    @Binds
    @Singleton
    BrigadierCommand advancedmanhuntCommand(AdvancedManHuntCommand command);

    @Binds
    @Singleton
    BrigadierCommand debugCommand(DebugCommand command);

    @Binds
    @Singleton
    BrigadierCommand gameCommand(GameCommand command);

    @Binds
    @Singleton
    BrigadierCommand templateCommand(TemplateCommand command);
}
