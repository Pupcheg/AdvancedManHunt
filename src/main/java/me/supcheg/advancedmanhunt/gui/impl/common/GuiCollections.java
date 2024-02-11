package me.supcheg.advancedmanhunt.gui.impl.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Positionable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiCollections {

    @NotNull
    @Contract("_ -> new")
    public static <T extends Positionable> Map<At, List<T>> buildSortedConsumersMap(@NotNull List<T> list) {
        Map<At, List<T>> map = new EnumMap<>(At.class);
        for (At at : At.values()) {
            map.put(at, list.stream().filter(l -> l.getAt() == at).sorted().toList());
        }

        return map;
    }

    @NotNull
    @Contract("_ -> param1")
    public static <T extends Comparable<T>> List<T> sortAndTrim(@NotNull List<T> list) {
        list.sort(Comparator.naturalOrder());
        if (list instanceof ArrayList<T> arrayList) {
            arrayList.trimToSize();
        }
        return list;
    }
}
