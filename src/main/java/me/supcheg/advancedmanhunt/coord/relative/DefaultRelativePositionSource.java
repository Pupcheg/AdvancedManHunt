package me.supcheg.advancedmanhunt.coord.relative;

import io.papermc.paper.math.Position;
import lombok.Data;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
@Data
final class DefaultRelativePositionSource implements RelativePositionSource {
    private final WorldReference world;
    private final Position offset;
    private final PositionBox box;

    @NotNull
    @Override
    public WorldReference world() {
        return world;
    }

    @NotNull
    @Override
    public RelativePosition fromAbsolute(@NotNull Position position) {
        assertFits(position);
        return new DefaultRelativePosition(position, position.offset(offset.x(), offset.y(), offset.z()));
    }

    @NotNull
    @Override
    public Position offset() {
        return offset;
    }

    @NotNull
    @Override
    public PositionBox box() {
        return box;
    }

    @Data
    final class DefaultRelativePosition implements RelativePosition {
        private final Position absolute;
        private final Position relative;

        @NotNull
        @Override
        public RelativePositionSource source() {
            return DefaultRelativePositionSource.this;
        }

        @NotNull
        @Override
        public Position relative() {
            return relative;
        }

        @NotNull
        @Override
        public Position absolute() {
            return absolute;
        }
    }
}
