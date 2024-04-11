package me.supcheg.advancedmanhunt.command.exception;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandAssertions {
    @NotNull
    @Contract("!null, _ -> param1; null, _ -> fail")
    public static <T> T requireNonNull(@Nullable T obj, @NotNull String argName) throws CommandSyntaxException {
        if (obj == null) {
            throw CustomExceptions.NULL.create(argName);
        }
        return obj;
    }
}
