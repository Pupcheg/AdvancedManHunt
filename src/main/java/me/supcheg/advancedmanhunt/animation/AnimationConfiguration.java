package me.supcheg.advancedmanhunt.animation;

import org.jetbrains.annotations.NotNull;

public interface AnimationConfiguration {

    int getInt(@NotNull String key, int defaultValue);

    double getDouble(@NotNull String key, double defaultValue);

    boolean getBoolean(@NotNull String key, boolean defaultValue);

}
