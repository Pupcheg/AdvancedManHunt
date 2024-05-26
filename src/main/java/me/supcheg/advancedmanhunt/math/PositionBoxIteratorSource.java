package me.supcheg.advancedmanhunt.math;

import io.papermc.paper.math.BlockPosition;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface PositionBoxIteratorSource {
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    Iterator<BlockPosition> iterator(@NotNull PositionBox box);

    long size(@NotNull PositionBox box);

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default Iterable<BlockPosition> iterable(@NotNull PositionBox box) {
        return () -> iterator(box);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default Stream<BlockPosition> stream(@NotNull PositionBox box) {
        return StreamSupport.stream(Spliterators.spliterator(iterator(box), size(box), 0), false);
    }
}
