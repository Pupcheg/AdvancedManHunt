package me.supcheg.advancedmanhunt.command.resolver;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.supcheg.advancedmanhunt.coord.Distance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

public class DistanceResolver implements DefaultResolver<Distance> {

    private final Int2ObjectMap<IntFunction<Distance>> char2function;
    private final List<String> completions;

    public DistanceResolver() {
        char2function = new Int2ObjectOpenHashMap<>();
        char2function.put('r', Distance::ofRegions);
        char2function.put('R', Distance::ofRegions);
        char2function.put('c', Distance::ofChunks);
        char2function.put('C', Distance::ofChunks);
        char2function.put('b', Distance::ofBlocks);
        char2function.put('B', Distance::ofBlocks);

        completions = char2function.keySet().intStream()
                .mapToObj(c -> String.valueOf((char) c))
                .toList();
    }

    @NotNull
    @Override
    public Distance getContext(@NotNull BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String argument = context.getFirstArg();

        if (argument == null || argument.isBlank()) {
            throw new InvalidCommandArgument("Distance must be a number");
        }

        char lastChar = argument.charAt(argument.length() - 1);

        var function = char2function.get(lastChar);

        Distance distance;
        if (function == null) {
            distance = Distance.ofBlocks(tryParseInt(argument));
        } else {
            int rawDistance = tryParseInt(argument.substring(0, argument.length() - 1));
            distance = function.apply(rawDistance);
        }
        return distance;
    }

    private int tryParseInt(@NotNull String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw new InvalidCommandArgument("Distance must be a number");
        }
    }

    @Nullable
    @Override
    public Collection<String> getCompletions(@NotNull BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        String partial = context.getInput();

        if (!partial.isEmpty()) {
            char lastChar = partial.charAt(partial.length() - 1);

            if (!char2function.containsKey(lastChar)) {
                return completions;
            }
        }

        return null;
    }
}
