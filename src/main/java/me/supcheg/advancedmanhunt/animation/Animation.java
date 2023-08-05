package me.supcheg.advancedmanhunt.animation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(onMethod_ = {@NotNull})
@EqualsAndHashCode
public abstract class Animation {
    private final String key;
    private final Component title;

    public abstract void play(@NotNull Location centerLocation);
}
