package me.supcheg.advancedmanhunt.math;

import io.papermc.paper.math.Position;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static me.supcheg.advancedmanhunt.math.builder.PositionBuilder.position;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Positions {
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Position sameXZ(int xz) {
        return Position.block(xz, 0, xz);
    }

    public static boolean equals(@Nullable Position first, @Nullable Position second) {
        if (Objects.equals(first, second)) {
            return true;
        }

        if (first != null && second != null) {
            PositionBuilder firstBuilder = position(first);
            PositionBuilder secondBuilder = position(second);
            return firstBuilder.equals(secondBuilder);
        }
        return false;
    }
}
