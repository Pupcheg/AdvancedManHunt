package me.supcheg.advancedmanhunt.coord;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CoordRangeIterator implements Iterator<Coord> {
    private final Coord start;
    private final Coord end;

    private int curX;
    private int curZ;

    CoordRangeIterator(@NotNull Coord start, @NotNull Coord end) {
        this.start = start;
        this.end = end;

        this.curX = start.getX() - 1;
        this.curZ = start.getZ();
    }

    @NotNull
    public Coord getStart() {
        return start;
    }

    @NotNull
    public Coord getEnd() {
        return end;
    }

    public int allCount() {
        return (end.getX() - start.getX() + 1) * (end.getZ() - start.getZ() + 1);
    }

    public int leftCount() {
        return (end.getX() - curX + 1) * (end.getZ() - curZ + 1);
    }

    @Override
    public boolean hasNext() {
        return curX < end.getX() || curZ < end.getZ();
    }

    @NotNull
    @Override
    public Coord next() {
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
    public Coord getCoord() {
        return Coord.coord(curX, curZ);
    }

    public int getX() {
        return curX;
    }

    public int getZ() {
        return curZ;
    }
}
