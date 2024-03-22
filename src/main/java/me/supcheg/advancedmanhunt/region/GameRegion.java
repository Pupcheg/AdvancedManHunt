package me.supcheg.advancedmanhunt.region;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.papermc.paper.math.Position;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.coord.CoordRangeIterator;
import me.supcheg.advancedmanhunt.coord.Coords;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Setter
@Getter
public class GameRegion {
    private final WorldReference worldReference;
    private final Coord startRegion;
    private final Coord endRegion;

    private final Coord startChunk;
    private final Coord endChunk;

    private final Coord startBlock;
    private final Coord endBlock;

    private final Coord centerBlock;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final AtomicBoolean isReserved;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final AtomicBoolean isBusy;

    public GameRegion(@NotNull WorldReference worldReference, @NotNull Coord startRegion, @NotNull Coord endRegion) {
        this.worldReference = worldReference;
        this.isReserved = new AtomicBoolean();
        this.isBusy = new AtomicBoolean();

        this.startRegion = startRegion;
        this.endRegion = endRegion;

        this.startChunk = startRegion.map(Coords::getFirstChunkInRegion);
        this.endChunk = endRegion.map(Coords::getLastChunkInRegion);

        this.startBlock = startChunk.map(Coords::getFirstBlockInChunk);
        this.endBlock = endChunk.map(Coords::getLastBlockInChunk);

        this.centerBlock = startBlock.average(endBlock);
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

    public boolean load() {
        World world = worldReference.getWorld();

        for (CoordRangeIterator it = iterateChunks(); it.hasNext(); it.moveNext()) {
            boolean loadResult = world.loadChunk(it.getX(), it.getZ(), true);

            if (!loadResult) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean unload() {
        World world = worldReference.getWorld();

        for (CoordRangeIterator it = iterateChunks(); it.hasNext(); it.moveNext()) {
            boolean unloadResult = world.unloadChunk(it.getX(), it.getZ(), false);

            if (!unloadResult) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public World getWorld() {
        return worldReference.getWorld();
    }

    @CanIgnoreReturnValue
    @Nullable
    @Contract("_ -> param1")
    public Location addDelta(@NotNull Location location) {
        return location.add(centerBlock.getX(), 0, centerBlock.getZ());
    }

    @Nullable
    @Contract("_ ->new")
    public ImmutableLocation withDelta(@NotNull ImmutableLocation location) {
        return location.add(centerBlock);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Coord addDelta(@NotNull Coord coord) {
        return coord.add(centerBlock);
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> param1")
    public Location removeDelta(@NotNull Location location) {
        return location.subtract(centerBlock.getX(), 0, centerBlock.getZ());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Coord removeDelta(@NotNull Coord coord) {
        return coord.subtract(centerBlock);
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public CoordRangeIterator iterateBlocks() {
        return Coords.iterateRangeInclusive(startBlock, endBlock);
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public CoordRangeIterator iterateChunks() {
        return Coords.iterateRangeInclusive(startChunk, endChunk);
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public CoordRangeIterator iterateRegions() {
        return Coords.iterateRangeInclusive(startRegion, endRegion);
    }

    @SuppressWarnings("UnstableApiUsage")
    public boolean contains(@NotNull Position pos) {
        Objects.requireNonNull(pos, "pos");
        return startBlock.getX() <= pos.x() && pos.x() <= endBlock.getX() &&
                startBlock.getZ() <= pos.z() && pos.z() <= endBlock.getZ();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GameRegion that)) {
            return false;
        }

        return worldReference.equals(that.worldReference)
                && startRegion.equals(that.startRegion)
                && endRegion.equals(that.endRegion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                worldReference,
                startRegion,
                endRegion
        );
    }

    @Override
    public String toString() {
        return "GameRegion{"
                + "worldReference=" + worldReference
                + ", startRegion=" + startRegion
                + ", endRegion=" + endRegion
                + ", startChunk=" + startChunk
                + ", endChunk=" + endChunk
                + ", startBlock=" + startBlock
                + ", endBlock=" + endBlock
                + ", centerBlock=" + centerBlock
                + ", isReserved=" + isReserved
                + ", isBusy=" + isBusy
                + '}';
    }
}
