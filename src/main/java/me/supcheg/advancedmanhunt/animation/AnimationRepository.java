package me.supcheg.advancedmanhunt.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface AnimationRepository {

    boolean addAnimation(@NotNull Animation animation);

    @NotNull
    @UnmodifiableView
    Collection<Animation> getAnimations();

    @Nullable
    Animation getAnimation(@NotNull String key);

}
