package me.supcheg.advancedmanhunt.math.builder;

import io.papermc.paper.math.Position;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface PositionBuilderApplicable {

    @NotNull
    @Contract("_ -> new")
    static PositionBuilderApplicable builderApplicable(@NotNull Position position) {
        Objects.requireNonNull(position, "position");
        return switch (position) {
            case PositionBuilderApplicable applicable -> applicable;
            case Location location -> new LocationPositionBuilderApplicable(location);
            default -> new AnyPositionPositionBuilderApplicable(position);
        };
    }

    void applyTo(@NotNull PositionBuilder builder);
}
