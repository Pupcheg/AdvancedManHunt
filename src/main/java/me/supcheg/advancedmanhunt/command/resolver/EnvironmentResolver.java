package me.supcheg.advancedmanhunt.command.resolver;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import org.bukkit.World.Environment;
import org.jetbrains.annotations.NotNull;

public class EnvironmentResolver implements DefaultResolver<Environment> {
    @NotNull
    @Override
    public Environment getContext(@NotNull BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        return Environment.valueOf(context.getFirstArg().toUpperCase());
    }
}
