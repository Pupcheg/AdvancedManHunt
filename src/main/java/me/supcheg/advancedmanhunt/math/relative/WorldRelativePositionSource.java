package me.supcheg.advancedmanhunt.math.relative;

import io.papermc.paper.math.Position;
import lombok.Data;
import me.supcheg.advancedmanhunt.math.PositionBox;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.NotNull;

@Data
final class WorldRelativePositionSource implements RelativePositionSource {
    private static final Position OFFSET = Position.FINE_ZERO;
    private static final PositionBox BOX = PositionBox.box(
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
    public RelativePosition absolute(@NotNull Position position) {
        return new WorldRelativePosition(
                PositionBuilder.position()
                        .world(world)
                        .xyz(position)
        );
    }

    @NotNull
    @Override
    public RelativePosition relative(@NotNull Position position) {
        return absolute(position);
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

    @Data
    final class WorldRelativePosition implements RelativePosition {
        private final PositionBuilder position;

        @NotNull
        @Override
        public RelativePositionSource source() {
            return WorldRelativePositionSource.this;
        }

        @NotNull
        @Override
        public PositionBuilder builderRelative() {
            return position.clone();
        }

        @NotNull
        @Override
        public PositionBuilder builderAbsolute() {
            return position.clone();
        }
    }
}
