package me.supcheg.advancedmanhunt.animation.impl;

import me.supcheg.advancedmanhunt.animation.AnimationConfiguration;
import org.jetbrains.annotations.NotNull;

public enum EmptyAnimationConfiguration implements AnimationConfiguration {
    INSTANCE;

    @Override
    public int getInt(@NotNull String key, int defaultValue) {
        return defaultValue;
    }

    @Override
    public double getDouble(@NotNull String key, double defaultValue) {
        return defaultValue;
    }

    @Override
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        return defaultValue;
    }
}
