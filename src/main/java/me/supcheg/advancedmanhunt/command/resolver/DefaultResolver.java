package me.supcheg.advancedmanhunt.command.resolver;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions.AsyncCommandCompletionHandler;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface DefaultResolver<T> extends ContextResolver<T, BukkitCommandExecutionContext>,
                                            AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {

    @NotNull
    @Override
    T getContext(@NotNull BukkitCommandExecutionContext context) throws InvalidCommandArgument;

    @Nullable
    @Override
    default Collection<String> getCompletions(@NotNull BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return null;
    }
}
