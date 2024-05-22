package me.supcheg.advancedmanhunt.math;

import io.papermc.paper.math.Position;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class PositionBox {
    private final Position min;
    private final Position max;

    @NotNull
    @Contract("_, _ -> new")
    public static PositionBox box(@NotNull Position corner1, @NotNull Position corner2) {
        Position min = Position.fine(
                Math.min(corner1.x(), corner2.x()),
                Math.min(corner1.y(), corner2.y()),
                Math.min(corner1.z(), corner2.z())
        );
        Position max = Position.fine(
                Math.max(corner1.x(), corner2.x()),
                Math.max(corner1.y(), corner2.y()),
                Math.max(corner1.z(), corner2.z())
        );
        return new PositionBox(min, max);
    }

    public boolean includes(@NotNull Position pos) {
        Objects.requireNonNull(pos, "pos");

        if (pos.x() < min.x() || pos.y() < min.y() || pos.z() < min.z()) {
            return false;
        }

        return pos.x() <= max.x() && pos.y() <= max.y() && pos.z() <= max.z();
    }

    public void assertIncludes(@NotNull Position pos) {
        if (!includes(pos)) {
            throw new IllegalArgumentException("Position %s doesn't fit in %s".formatted(pos, this));
        }
    }
}