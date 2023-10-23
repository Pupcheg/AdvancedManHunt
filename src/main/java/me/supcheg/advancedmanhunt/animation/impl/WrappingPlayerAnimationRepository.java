package me.supcheg.advancedmanhunt.animation.impl;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.animation.Animation;
import me.supcheg.advancedmanhunt.animation.PlayerAnimationsRepository;
import me.supcheg.advancedmanhunt.exception.AnimationNotAvailableException;
import me.supcheg.advancedmanhunt.exception.AnimationNotRegisteredException;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class WrappingPlayerAnimationRepository implements PlayerAnimationsRepository {
    private final EntityRepository<Animation, String> animationRepository;
    private final EntityRepository<AnimationUser, UUID> animationUserRepository;

    @Nullable
    @Override
    public Animation getSelectedAnimation(@NotNull UUID uniqueId, @NotNull String object) {
        return getAnimation(getUser(uniqueId).getObjectToSelectedAnimation().get(object));
    }

    @Override
    public void setSelectedAnimation(@NotNull UUID uniqueId, @NotNull String object, @NotNull Animation animation) {
        assertAnimationRegistered(animation);
        AnimationUser user = getUser(uniqueId);

        assertIsAvailable(user, animation);
        user.getObjectToSelectedAnimation().put(object, animation.getKey());
    }

    @NotNull
    @Override
    public Collection<Animation> getAvailableAnimations(@NotNull UUID uniqueId) {
        return getUser(uniqueId).getAvailableAnimations().stream()
                .map(this::getAnimation)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void addAvailableAnimation(@NotNull UUID uniqueId, @NotNull Animation animation) {
        assertAnimationRegistered(animation);
        getUser(uniqueId).getAvailableAnimations().add(animation.getKey());
    }

    @Override
    public void removeAvailableAnimation(@NotNull UUID uniqueId, @NotNull Animation animation) {
        assertAnimationRegistered(animation);

        getUser(uniqueId).getAvailableAnimations()
                .remove(animation.getKey());
    }

    private void assertAnimationRegistered(@NotNull Animation animation) {
        if (!animationRepository.containsEntity(animation)) {
            throw new AnimationNotRegisteredException(animation.getKey() + " is not registered");
        }
    }

    private void assertIsAvailable(@NotNull AnimationUser animationUser, @NotNull Animation animation) {
        if (!animationUser.getAvailableAnimations().contains(animation.getKey())) {
            throw new AnimationNotAvailableException(animationUser.getUniqueId() + " is not allowed to use " + animation.getKey());
        }
    }

    @NotNull
    private AnimationUser getUser(@NotNull UUID uniqueId) {
        return animationUserRepository.getOrCreateEntity(uniqueId, AnimationUser::new);
    }

    @Nullable
    private Animation getAnimation(@Nullable String key) {
        return key == null ? null : animationRepository.getEntity(key);
    }
}
