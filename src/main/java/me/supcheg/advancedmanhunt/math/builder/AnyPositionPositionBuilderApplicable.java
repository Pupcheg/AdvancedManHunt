package me.supcheg.advancedmanhunt.math.builder;

import io.papermc.paper.math.Position;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
class AnyPositionPositionBuilderApplicable implements PositionBuilderApplicable {
    private final Position position;

    @Override
    public void applyTo(@NotNull PositionBuilder builder) {
        builder.xyz(position);
    }
}
