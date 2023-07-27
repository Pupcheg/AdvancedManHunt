package me.supcheg.advancedmanhunt.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface PlayerAnimationsRepository {
    @Nullable
    Animation getAnimation(@NotNull UUID uniqueId, @NotNull String object);

    void setAnimation(@NotNull UUID uniqueId, @NotNull String object, @NotNull Animation animation);

    @NotNull
    AnimationConfiguration getAnimationConfiguration(@NotNull UUID uniqueId, @NotNull Animation animation);

    void setAnimationConfiguration(@NotNull UUID uniqueId, @NotNull String object, @NotNull AnimationConfiguration configuration);

    @NotNull
    Collection<Animation> getAvailableAnimations(@NotNull UUID uniqueId);
}
