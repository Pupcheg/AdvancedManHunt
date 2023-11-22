package me.supcheg.advancedmanhunt.structure;

import me.supcheg.advancedmanhunt.animation.Animation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class DummyAnimation extends Animation {
    public DummyAnimation(@NotNull String key) {
        super(key, text(key));
    }

    @Override
    public void play(@NotNull Location centerLocation) {
    }
}
