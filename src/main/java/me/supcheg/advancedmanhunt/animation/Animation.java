package me.supcheg.advancedmanhunt.animation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.supcheg.advancedmanhunt.animation.impl.EmptyAnimationConfiguration;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(onMethod_ = {@NotNull})
@EqualsAndHashCode
public abstract class Animation {
    private final String key;

    public void play(@NotNull Location centerLocation) {
        play(centerLocation, EmptyAnimationConfiguration.INSTANCE);
    }

    public abstract void play(@NotNull Location centerLocation, @NotNull AnimationConfiguration configuration);
}
