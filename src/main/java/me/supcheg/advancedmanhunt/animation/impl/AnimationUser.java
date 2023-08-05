package me.supcheg.advancedmanhunt.animation.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public final class AnimationUser {
    private final UUID uniqueId;
    private Map<String, String> objectToSelectedAnimation;
    private List<String> availableAnimations;

    public AnimationUser(@NotNull UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.objectToSelectedAnimation = new HashMap<>();
        this.availableAnimations = new ArrayList<>();
    }
}
