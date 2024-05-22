package me.supcheg.advancedmanhunt.math.builder;

import lombok.Data;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@Data
class LocationPositionBuilderApplicable implements PositionBuilderApplicable {
    private final Location location;

    @Override
    public void applyTo(@NotNull PositionBuilder builder) {
        builder.world(location.getWorld())
                .xyz(location)
                .yaw(location.getYaw())
                .pitch(location.getPitch());
    }
}
