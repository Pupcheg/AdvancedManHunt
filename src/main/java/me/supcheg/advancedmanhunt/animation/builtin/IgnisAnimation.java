package me.supcheg.advancedmanhunt.animation.builtin;

import me.supcheg.advancedmanhunt.animation.Animation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.NAMESPACE;

public class IgnisAnimation extends Animation {
    private static final int TONGUES_COUNT = 5;
    private static final double TONGUE_LENGTH = 1.75;
    private static final double TONGUE_HEIGHT = 2.5;
    private static final double DEGREES_BETWEEN_TONGUES = 360d / TONGUES_COUNT;

    public IgnisAnimation() {
        super(NAMESPACE + ":builtin_ignis");
    }

    @Override
    public void play(@NotNull Location centerLocation) {
        // TODO: 27.07.2023
    }
}
