package me.supcheg.advancedmanhunt.region;

import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.math.PositionBox;
import me.supcheg.advancedmanhunt.math.distance.DistancePair;
import me.supcheg.advancedmanhunt.math.relative.RelativePositionSource;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameRegion {
    private final RelativePositionSource positionSource;
    private final DistancePair start;
    private final DistancePair end;
    private final AtomicBoolean isReserved;
    private final AtomicBoolean isBusy;

    public GameRegion(@NotNull WorldReference worldReference, @NotNull DistancePair start, @NotNull DistancePair end) {
        this.start = start;
        this.end = end;

        this.isReserved = new AtomicBoolean();
        this.isBusy = new AtomicBoolean();

        Position offset = Position.fine(
                (start.getBlockX() + end.getBlockZ()) / 2d,
                0,
                (end.getBlockZ() + end.getBlockZ()) / 2d
        );
        World world = worldReference.getWorld();

        this.positionSource = RelativePositionSource.relativePositionSource()
                .world(worldReference)
                .offset(offset)
                .box(PositionBox.box(
                        start.offset(0, world.getMinHeight(), 0),
                        end.offset(0, world.getMaxHeight() - 1, 0)
                ))
                .build();
    }

    @NotNull
    public RelativePositionSource positionSource() {
        return positionSource;
    }

    public boolean isReserved() {
        return isReserved.get();
    }

    public void setReserved(boolean busy) {
        isReserved.set(busy);
    }

    public boolean isBusy() {
        return isBusy.get();
    }

    public void setBusy(boolean busy) {
        isBusy.set(busy);
    }

    @NotNull
    public DistancePair start() {
        return start;
    }

    @NotNull
    public DistancePair end() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GameRegion that)) {
            return false;
        }

        return positionSource.world().equals(that.positionSource.world())
               && start.equals(that.start)
               && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionSource.world(), start, end);
    }
}
