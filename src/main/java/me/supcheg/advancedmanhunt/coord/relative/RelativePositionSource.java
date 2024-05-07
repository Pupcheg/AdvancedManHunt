package me.supcheg.advancedmanhunt.coord.relative;

import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public interface RelativePositionSource {

    @NotNull
    WorldReference world();

    @NotNull
    RelativePosition fromAbsolute(@NotNull Position position);

    @NotNull
    default RelativePosition migrate(@NotNull RelativePosition position) {
        if (position.source() == this) {
            return position;
        }
        return fromAbsolute(position.absolute());
    }
}
