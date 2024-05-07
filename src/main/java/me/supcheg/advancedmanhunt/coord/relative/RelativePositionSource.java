package me.supcheg.advancedmanhunt.coord.relative;

import io.papermc.paper.math.Position;
import lombok.Data;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public interface RelativePositionSource {

    @NotNull
    @Contract(value = "-> new", pure = true)
    static RelativePositionSourceBuilder relativePositionSource() {
        return new RelativePositionSourceBuilder();
    }

    @NotNull
    WorldReference world();

    @NotNull
    RelativePosition fromAbsolute(@NotNull Position position);

    @NotNull
    Position offset();

    @NotNull
    PositionBox box();

    default boolean isFits(@NotNull Position pos) {
        Objects.requireNonNull(pos, "position");
        PositionBox box = box();

        Position minPos = box.getMinPos();
        if (pos.x() < minPos.x() || pos.y() < minPos.y() || pos.z() < minPos.z()) {
            return false;
        }

        Position maxPos = box.getMaxPos();
        return pos.x() <= maxPos.x() && pos.y() <= maxPos.y() && pos.z() <= maxPos.z();
    }

    default void assertFits(@NotNull Position position) {
        if (!isFits(position)) {
            throw new IllegalArgumentException("Position %s doesn't fit in %s".formatted(position, this));
        }
    }

    @NotNull
    default RelativePosition migrate(@NotNull RelativePosition position) {
        if (position.source() == this) {
            return position;
        }
        Position absolute = position.absolute();
        assertFits(absolute);
        return fromAbsolute(absolute);
    }

    @Data(staticConstructor = "of")
    class PositionBox {
        private final Position minPos;
        private final Position maxPos;
    }
}
