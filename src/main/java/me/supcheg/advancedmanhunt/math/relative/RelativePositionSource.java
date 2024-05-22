package me.supcheg.advancedmanhunt.math.relative;

import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.math.PositionBox;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface RelativePositionSource {

    @NotNull
    @Contract(value = "-> new", pure = true)
    static RelativePositionSourceBuilder relativePositionSource() {
        return new RelativePositionSourceBuilder();
    }

    @NotNull
    WorldReference world();

    @NotNull
    RelativePosition absolute(@NotNull Position position);

    @NotNull
    RelativePosition relative(@NotNull Position position);

    @NotNull
    default RelativePosition relative(double x, double y, double z) {
        return relative(Position.fine(x, y, z));
    }

    @NotNull
    Position offset();

    @NotNull
    PositionBox box();

    @NotNull
    default RelativePosition migrate(@NotNull RelativePosition position) {
        if (position.source() == this) {
            return position;
        }
        Position absolute = position.absolute();
        box().assertIncludes(absolute);
        return absolute(absolute);
    }
}
