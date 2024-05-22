package me.supcheg.advancedmanhunt.assertion;

import io.papermc.paper.math.Position;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.math.PositionBox;
import me.supcheg.advancedmanhunt.math.Positions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AssertionFailureBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PositionAssertions {
    public static void assertIncludes(@NotNull PositionBox box, @NotNull Position pos) {
        assertTrue(box.includes(pos), () -> "%s is not in %s".formatted(pos, box));
    }

    public static void assertPositionsEquals(@Nullable Position expected, @Nullable Position actual) {
        if (!Positions.equals(expected, actual)) {
            AssertionFailureBuilder.assertionFailure()
                    .expected(expected)
                    .actual(actual)
                    .buildAndThrow();
        }
    }
}
