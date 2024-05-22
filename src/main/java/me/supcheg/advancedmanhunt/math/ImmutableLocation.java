package me.supcheg.advancedmanhunt.math;

import io.papermc.paper.math.FinePosition;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilderApplicable;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@Getter(AccessLevel.NONE)
public class ImmutableLocation implements FinePosition, PositionBuilderApplicable {
    private final WorldReference world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    @Nullable
    public WorldReference world() {
        return world;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    public float pitch() {
        return pitch;
    }

    public float yaw() {
        return yaw;
    }

    @Override
    public void applyTo(@NotNull PositionBuilder builder) {
        builder
                .world(world)
                .xyz(x, y, z)
                .yaw(yaw)
                .pitch(pitch);
    }
}
