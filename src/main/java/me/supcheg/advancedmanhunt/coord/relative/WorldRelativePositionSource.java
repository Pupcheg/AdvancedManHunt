package me.supcheg.advancedmanhunt.coord.relative;

import io.papermc.paper.math.Position;
import lombok.Data;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
@Data
final class WorldRelativePositionSource implements RelativePositionSource {
    private static final Position OFFSET = Position.FINE_ZERO;
    private static final PositionBox BOX = PositionBox.of(
            Position.fine(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
            Position.fine(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)
    );
    private final WorldReference world;

    @NotNull
    @Override
    public WorldReference world() {
        return world;
    }

    @NotNull
    @Override
    public RelativePosition fromAbsolute(@NotNull Position position) {
        Objects.requireNonNull(position, "position");
        return new WorldRelativePosition(position);
    }

    @NotNull
    @Override
    public Position offset() {
        return OFFSET;
    }

    @NotNull
    @Override
    public PositionBox box() {
        return BOX;
    }

    @Override
    public boolean isFits(@NotNull Position pos) {
        Objects.requireNonNull(pos, "position");
        return true;
    }

    @Data
    final class WorldRelativePosition implements RelativePosition {
        private final Position position;

        @NotNull
        @Override
        public RelativePositionSource source() {
            return WorldRelativePositionSource.this;
        }

        @NotNull
        @Override
        public Position relative() {
            return position;
        }

        @NotNull
        @Override
        public Position absolute() {
            return position;
        }
    }
}
