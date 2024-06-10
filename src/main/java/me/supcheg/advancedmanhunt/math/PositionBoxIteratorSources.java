package me.supcheg.advancedmanhunt.math;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public enum PositionBoxIteratorSources implements PositionBoxIteratorSource {
    XYZ {
        @NotNull
        @Override
        public Iterator<BlockPosition> iterator(@NotNull PositionBox box) {
            BlockPosition min = box.getMin();
            BlockPosition max = box.getMax();

            return new Iterator<>() {
                private int curY = min.blockY() - 1;
                private int curX = min.blockX();
                private int curZ = min.blockZ();

                @Override
                public boolean hasNext() {
                    return curY < max.blockY()
                           || curX < max.blockX()
                           || curZ < max.blockZ();
                }

                @NotNull
                @Override
                public BlockPosition next() {
                    if (++curY > max.blockY()) {
                        curY = min.blockY();
                        if (++curX > max.blockX()) {
                            curX = min.blockX();

                            if (++curZ > max.blockZ()) {
                                throw new NoSuchElementException();
                            }
                        }
                    }

                    return Position.block(curX, curY, curZ);
                }
            };
        }

        @Override
        public long size(@NotNull PositionBox box) {
            BlockPosition max = box.getMax();
            BlockPosition min = box.getMin();
            return (long) (max.blockX() - min.blockX() + 1) *
                   (max.blockY() - min.blockY() + 1) *
                   (max.blockZ() - min.blockZ() + 1);
        }
    },
    XZ {
        @NotNull
        @Override
        public Iterator<BlockPosition> iterator(@NotNull PositionBox box) {
            BlockPosition min = box.getMin();
            BlockPosition max = box.getMax();

            return new Iterator<>() {
                private int curX = min.blockX() - 1;
                private int curZ = min.blockZ();

                @Override
                public boolean hasNext() {
                    return curX < max.blockX() || curZ < max.blockZ();
                }

                @NotNull
                @Override
                public BlockPosition next() {
                    if (++curX > max.blockX()) {
                        curX = min.blockX();

                        if (++curZ > max.blockZ()) {
                            throw new NoSuchElementException();
                        }
                    }

                    return Position.block(curX, 0, curZ);
                }
            };
        }

        @Override
        public long size(@NotNull PositionBox box) {
            BlockPosition max = box.getMax();
            BlockPosition min = box.getMin();
            return (long) (max.blockX() - min.blockX() + 1) *
                   (max.blockZ() - min.blockZ() + 1);
        }
    }
}
