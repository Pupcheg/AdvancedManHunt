package me.supcheg.advancedmanhunt.command.resolver;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeyedCoordResolver implements DefaultResolver<KeyedCoord> {

    @NotNull
    @Override
    public KeyedCoord getContext(@NotNull BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        List<String> args = context.getArgs();
        return KeyedCoord.of(Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1)));
    }
}
