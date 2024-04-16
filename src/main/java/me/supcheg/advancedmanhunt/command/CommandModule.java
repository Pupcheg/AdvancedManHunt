package me.supcheg.advancedmanhunt.command;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.command.argument.AdvancedGuiArgument;
import me.supcheg.advancedmanhunt.command.argument.ManHuntGameArgument;
import me.supcheg.advancedmanhunt.command.argument.ManHuntRoleArgument;
import me.supcheg.advancedmanhunt.command.argument.RealEnvironmentArgument;
import me.supcheg.advancedmanhunt.command.argument.TemplateArgument;

import javax.inject.Singleton;

@Module
public interface CommandModule {
    @Binds
    @Singleton
    BukkitBrigadierCommand advancedmanhuntCommand(AdvancedManHuntCommand command);

    @Binds
    @Singleton
    BukkitBrigadierCommand debugCommand(DebugCommand command);

    @Binds
    @Singleton
    BukkitBrigadierCommand gameCommand(GameCommand command);

    @Binds
    @Singleton
    BukkitBrigadierCommand templateCommand(TemplateCommand command);


    @Binds
    @Singleton
    Object advancedGuiArgument(AdvancedGuiArgument advancedGuiArgument);

    @Binds
    @Singleton
    Object manhuntGameArgument(ManHuntGameArgument manhuntGameArgument);

    @Binds
    @Singleton
    Object manhuntRoleArgument(ManHuntRoleArgument manhuntRoleArgument);

    @Binds
    @Singleton
    Object realEnvironmentArgument(RealEnvironmentArgument realEnvironmentArgument);

    @Binds
    @Singleton
    Object templateArgument(TemplateArgument templateArgument);
}
