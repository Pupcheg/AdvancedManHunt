package me.supcheg.advancedmanhunt.animation.impl;

import me.supcheg.advancedmanhunt.animation.Animation;
import me.supcheg.advancedmanhunt.animation.AnimationRepository;
import me.supcheg.advancedmanhunt.animation.builtin.FireworksAnimation;
import me.supcheg.advancedmanhunt.animation.builtin.IgnisAnimation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultAnimationRepository implements AnimationRepository {

    private final Map<String, Animation> key2animation;

    public DefaultAnimationRepository() {
        this.key2animation = new HashMap<>();

        addAnimation(new FireworksAnimation());
        addAnimation(new IgnisAnimation());
    }


    @Override
    public boolean addAnimation(@NotNull Animation animation) {
        return key2animation.putIfAbsent(animation.getKey(), animation) == animation;
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Collection<Animation> getAnimations() {
        return Collections.unmodifiableCollection(key2animation.values());
    }

    @Nullable
    @Override
    public Animation getAnimation(@NotNull String key) {
        return key2animation.get(key);
    }
}
