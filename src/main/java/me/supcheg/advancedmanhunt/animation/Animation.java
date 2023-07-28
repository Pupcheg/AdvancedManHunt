package me.supcheg.advancedmanhunt.animation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(onMethod_ = {@NotNull})
@EqualsAndHashCode
public abstract class Animation {
    private final String key;

    public abstract void play(@NotNull Location centerLocation);
}
