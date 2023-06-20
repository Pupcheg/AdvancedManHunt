package me.supcheg.advancedmanhunt.region;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.supcheg.advancedmanhunt.coord.CoordIterator;
import me.supcheg.advancedmanhunt.coord.CoordUtil;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Setter
@Getter
@ToString
public class GameRegion {
    private final WorldReference worldReference;
    private final KeyedCoord startRegion;
    private final KeyedCoord endRegion;

    private final KeyedCoord startChunk;
    private final KeyedCoord endChunk;

    private final KeyedCoord startBlock;
    private final KeyedCoord endBlock;

    private final Distance sideSize;

    private boolean isReserved;
    private boolean isBusy;

    public GameRegion(@NotNull WorldReference worldReference, @NotNull KeyedCoord startRegion, @NotNull KeyedCoord endRegion) {
        this.worldReference = worldReference;

        this.startRegion = startRegion;
        this.endRegion = endRegion;

        this.startChunk = CoordUtil.getFirstChunkInRegion(startRegion);
        this.endChunk = CoordUtil.getLastChunkInRegion(endRegion);

        this.startBlock = CoordUtil.getFirstBlockInChunk(startChunk);
        this.endBlock = CoordUtil.getLastBlockInChunk(endChunk);

        this.sideSize = Distance.ofRegions(endRegion.getX() - startRegion.getX() + 1);
    }

    public boolean load() {
        World world = worldReference.getWorld();

        for (CoordIterator it = iterateChunks(); it.hasNext(); it.moveNext()) {
            boolean loadResult = world.loadChunk(it.getX(), it.getZ(), true);

            if (!loadResult) {
                return false;
            }
        }
        return true;
    }

    public boolean unload() {
        World world = worldReference.getWorld();

        for (CoordIterator it = iterateChunks(); it.hasNext(); it.moveNext()) {
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
    @NotNull
    @Contract("_ -> param1")
    public Location addDelta(@NotNull Location location) {
        return location.add(startBlock.getX(), 0, startBlock.getZ());
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> param1")
    public Location removeDelta(@NotNull Location location) {
        return location.subtract(startBlock.getX(), 0, startBlock.getZ());
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public CoordIterator iterateBlocks() {
        return CoordUtil.iterateInclusive(startBlock, endBlock);
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public CoordIterator iterateChunks() {
        return CoordUtil.iterateInclusive(startChunk, endChunk);
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public CoordIterator iterateRegions() {
        return CoordUtil.iterateInclusive(startRegion, endRegion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GameRegion region)) {
            return false;
        }

        return worldReference.equals(region.worldReference)
                && startRegion.equals(region.startRegion)
                && endRegion.equals(region.endRegion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                worldReference,
                startRegion,
                endRegion
        );
    }
}
