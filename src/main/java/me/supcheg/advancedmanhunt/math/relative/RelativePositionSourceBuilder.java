package me.supcheg.advancedmanhunt.math.relative;

import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.math.PositionBox;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class RelativePositionSourceBuilder {
    private WorldReference world;
    private Position offset;
    private PositionBox box;

    RelativePositionSourceBuilder() {
    }

    @NotNull
    @Contract(value = "_ -> this")
    public RelativePositionSourceBuilder world(@NotNull WorldReference world) {
        Objects.requireNonNull(world, "world");
        this.world = world;
        return this;
    }

    @NotNull
    @Contract(value = "_ -> this")
    public RelativePositionSourceBuilder offset(@NotNull Position offset) {
        Objects.requireNonNull(offset, "offset");
        this.offset = offset;
        return this;
    }

    @NotNull
    @Contract(value = "_ -> this")
    public RelativePositionSourceBuilder box(@NotNull PositionBox box) {
        Objects.requireNonNull(box, "box");
        this.box = box;
        return this;
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public RelativePositionSource build() {
        Objects.requireNonNull(world, "world");

        if (offset == null && box == null) {
            return new WorldRelativePositionSource(world);
        }

        Objects.requireNonNull(offset, "offset");
        Objects.requireNonNull(box, "box");
        return new DefaultRelativePositionSource(world, offset, box);
    }
}
