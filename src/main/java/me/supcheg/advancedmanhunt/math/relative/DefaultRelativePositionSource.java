package me.supcheg.advancedmanhunt.math.relative;

import io.papermc.paper.math.Position;
import lombok.Data;
import me.supcheg.advancedmanhunt.math.PositionBox;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.NotNull;

@Data
final class DefaultRelativePositionSource implements RelativePositionSource {
    private final WorldReference world;
    private final Position offset;
    private final PositionBox absoluteBox;
    private final PositionBox relativeBox;

    public DefaultRelativePositionSource(@NotNull WorldReference world,
                                         @NotNull Position offset,
                                         @NotNull PositionBox box) {
        this.world = world;
        this.offset = offset;
        this.absoluteBox = box;
        this.relativeBox = PositionBox.box(
                box.getMin().offset(-offset.x(), -offset.y(), -offset.z()),
                box.getMax().offset(-offset.x(), -offset.y(), -offset.z())
        );
    }

    @NotNull
    @Override
    public WorldReference world() {
        return world;
    }

    @NotNull
    @Override
    public RelativePosition absolute(@NotNull Position position) {
        absoluteBox.assertIncludes(position);
        return new DefaultRelativePosition(
                PositionBuilder.position()
                        .world(world)
                        .xyz(position),
                PositionBuilder.position()
                        .world(world)
                        .xyz(position)
                        .offset(offset.x(), offset.y(), offset.z())
        );
    }

    @NotNull
    @Override
    public RelativePosition relative(@NotNull Position position) {
        relativeBox.assertIncludes(position);
        return new DefaultRelativePosition(
                PositionBuilder.position()
                        .world(world)
                        .xyz(position)
                        .offset(offset.x(), offset.y(), offset.z()),
                PositionBuilder.position()
                        .world(world)
                        .xyz(position)
        );
    }

    @NotNull
    @Override
    public Position offset() {
        return offset;
    }

    @NotNull
    @Override
    public PositionBox box() {
        return absoluteBox;
    }

    @Data
    final class DefaultRelativePosition implements RelativePosition {
        private final PositionBuilder absolute;
        private final PositionBuilder relative;

        @NotNull
        @Override
        public RelativePositionSource source() {
            return DefaultRelativePositionSource.this;
        }

        @NotNull
        @Override
        public PositionBuilder builderRelative() {
            return relative.clone();
        }

        @NotNull
        @Override
        public PositionBuilder builderAbsolute() {
            return absolute.clone();
        }
    }
}
