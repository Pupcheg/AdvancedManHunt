package me.supcheg.advancedmanhunt.animation.impl;

import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.animation.AnimationConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@AllArgsConstructor
public class MapBasedAnimationConfiguration implements AnimationConfiguration {

    private final Map<String, String> values;

    @Override
    public int getInt(@NotNull String key, int defaultValue) {
        String value = values.get(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    @Override
    public double getDouble(@NotNull String key, double defaultValue) {
        String value = values.get(key);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    @Override
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        String value = values.get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }
}
