package me.supcheg.advancedmanhunt.gui.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.impl.wrapped.Positionable;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GuiCollections {

    @NotNull
    public static <T extends Positionable> Map<At, List<T>> buildConsumersMap(@NotNull List<T> list) {
        Map<At, List<T>> map = new EnumMap<>(At.class);
        for (At at : At.values()) {
            map.put(at, list.stream().filter(l -> l.getAt() == at).sorted().toList());
        }

        return map;
    }
}
