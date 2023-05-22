package me.supcheg.advancedmanhunt.coord;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CoordIterator implements Iterator<KeyedCoord> {
    private final KeyedCoord start;
    private final KeyedCoord end;

    private int curX;
    private int curZ;

    CoordIterator(@NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        this.start = start;
        this.end = end;

        this.curX = start.getX() - 1;
        this.curZ = start.getZ();
    }

    @NotNull
    public KeyedCoord getStart() {
        return start;
    }

    @NotNull
    public KeyedCoord getEnd() {
        return end;
    }

    @Override
    public boolean hasNext() {
        return curX + 1 <= end.getX() || curZ + 1 <= end.getZ();
    }

    @NotNull
    @Override
    public KeyedCoord next() {
        moveNext();
        return getCoord();
    }

    public void moveNext() {
        if (++curX > end.getX()) {
            curX = start.getX();

            if (++curZ > end.getZ()) {
                throw new NoSuchElementException();
            }
        }
    }

    @NotNull
    public KeyedCoord getCoord() {
        return KeyedCoord.of(curX, curZ);
    }

    public int getX() {
        return curX;
    }

    public int getZ() {
        return curZ;
    }

    public long getKey() {
        return CoordUtil.getKey(curX, curZ);
    }

}
