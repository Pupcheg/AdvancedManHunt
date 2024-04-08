package me.supcheg.advancedmanhunt.animation;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(onMethod_ = {@NotNull})
@EqualsAndHashCode
public abstract class Animation {
    private final Key key;
    private final Component title;

    public abstract void play(@NotNull Location centerLocation);
}
