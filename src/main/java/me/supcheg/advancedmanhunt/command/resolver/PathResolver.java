package me.supcheg.advancedmanhunt.command.resolver;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class PathResolver implements DefaultResolver<Path> {

    @NotNull
    @Override
    public Path getContext(@NotNull BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        return Path.of(String.join(" ", context.getArgs()));
    }
}
