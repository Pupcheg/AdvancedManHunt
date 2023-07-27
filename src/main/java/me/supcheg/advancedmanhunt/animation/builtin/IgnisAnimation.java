package me.supcheg.advancedmanhunt.animation.builtin;

import me.supcheg.advancedmanhunt.animation.Animation;
import me.supcheg.advancedmanhunt.animation.AnimationConfiguration;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.NAMESPACE;

public class IgnisAnimation extends Animation {
    public IgnisAnimation() {
        super(NAMESPACE + ":builtin_ignis");
    }

    @Override
    public void play(@NotNull Location centerLocation, @NotNull AnimationConfiguration configuration) {
        int tonguesCount = configuration.getInt("tongues", 5);
        double tongueLength = configuration.getDouble("length", 1.75);
        double tongueHeight = configuration.getDouble("height", 2.5);

        double degreesBetweenTongues = 360d / tonguesCount;

        // TODO: 27.07.2023
    }
}
