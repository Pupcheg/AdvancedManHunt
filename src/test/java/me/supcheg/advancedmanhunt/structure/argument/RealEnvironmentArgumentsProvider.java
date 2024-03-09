package me.supcheg.advancedmanhunt.structure.argument;

import me.supcheg.advancedmanhunt.region.RealEnvironment;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static me.supcheg.advancedmanhunt.region.RealEnvironment.NETHER;
import static me.supcheg.advancedmanhunt.region.RealEnvironment.OVERWORLD;
import static me.supcheg.advancedmanhunt.region.RealEnvironment.THE_END;

public class RealEnvironmentArgumentsProvider implements ArgumentsProvider {
    @NotNull
    @Override
    public Stream<? extends Arguments> provideArguments(@NotNull ExtensionContext context) {
        return Stream.of(
                Arguments.of(OVERWORLD, new RealEnvironment[]{NETHER, THE_END}),
                Arguments.of(NETHER, new RealEnvironment[]{OVERWORLD, THE_END}),
                Arguments.of(THE_END, new RealEnvironment[]{OVERWORLD, NETHER})
        );
    }
}
