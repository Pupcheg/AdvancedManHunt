package me.supcheg.advancedmanhunt.math;

import io.papermc.paper.math.BlockPosition;
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
    private final BlockPosition min;
    private final BlockPosition max;

    @NotNull
    @Contract("_, _ -> new")
    public static PositionBox box(@NotNull Position corner1, @NotNull Position corner2) {
        BlockPosition min = Position.block(
                Math.min(corner1.blockX(), corner2.blockX()),
                Math.min(corner1.blockY(), corner2.blockY()),
                Math.min(corner1.blockZ(), corner2.blockZ())
        );
        BlockPosition max = Position.block(
                Math.max(corner1.blockX(), corner2.blockX()),
                Math.max(corner1.blockY(), corner2.blockY()),
                Math.max(corner1.blockZ(), corner2.blockZ())
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