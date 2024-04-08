package me.supcheg.advancedmanhunt.animation.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public final class AnimationUser {
    private final UUID uniqueId;
    private final Map<Key, Key> objectToSelectedAnimation = new HashMap<>();
    private final List<Key> availableAnimations = new ArrayList<>();
}
