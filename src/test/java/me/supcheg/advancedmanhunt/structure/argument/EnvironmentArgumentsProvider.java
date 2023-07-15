package me.supcheg.advancedmanhunt.structure.argument;

import org.bukkit.World.Environment;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class EnvironmentArgumentsProvider implements ArgumentsProvider {
    @NotNull
    @Override
    public Stream<? extends Arguments> provideArguments(@NotNull ExtensionContext context) {
        return Stream.of(
                Arguments.of(Environment.NORMAL, new Environment[]{Environment.NETHER, Environment.THE_END}),
                Arguments.of(Environment.NETHER, new Environment[]{Environment.NORMAL, Environment.THE_END}),
                Arguments.of(Environment.THE_END, new Environment[]{Environment.NORMAL, Environment.NETHER})
        );
    }
}
