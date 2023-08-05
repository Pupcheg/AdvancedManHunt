package me.supcheg.advancedmanhunt.animation.builtin;

import me.supcheg.advancedmanhunt.animation.Animation;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.NAMESPACE;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.TextColor.color;

public class FireworksAnimation extends Animation {
    public FireworksAnimation() {
        super(
                NAMESPACE + ":builtin_fireworks",
                translatable("advancedmanhunt.animation.builtin.fireworks", color(0xFF00BB))
        );
    }

    @Override
    public void play(@NotNull Location centerLocation) {
        for (int i = 0; i < 5; i++) {
            centerLocation.getWorld().spawnEntity(centerLocation, EntityType.FIREWORK, true);
        }
    }

}
