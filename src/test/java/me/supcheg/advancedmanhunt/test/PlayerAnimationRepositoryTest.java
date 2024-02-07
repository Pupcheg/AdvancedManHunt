package me.supcheg.advancedmanhunt.test;

import me.supcheg.advancedmanhunt.animation.Animation;
import me.supcheg.advancedmanhunt.animation.PlayerAnimationsRepository;
import me.supcheg.advancedmanhunt.animation.impl.AnimationUser;
import me.supcheg.advancedmanhunt.animation.impl.WrappingPlayerAnimationRepository;
import me.supcheg.advancedmanhunt.exception.AnimationNotAvailableException;
import me.supcheg.advancedmanhunt.exception.AnimationNotRegisteredException;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import me.supcheg.advancedmanhunt.storage.Repositories;
import me.supcheg.advancedmanhunt.util.ThreadSafeRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerAnimationRepositoryTest {
    private static final String OBJECT = "dummy";

    private EntityRepository<Animation, String> animationsRepository;

    private PlayerAnimationsRepository playerAnimationsRepository;

    private UUID uniqueId;
    private Animation animation;

    @BeforeEach
    void setup() {
        animationsRepository = Repositories.inMemory(Animation::getKey);
        EntityRepository<AnimationUser, UUID> animationUsersRepository = Repositories.inMemory(AnimationUser::getUniqueId);

        playerAnimationsRepository = new WrappingPlayerAnimationRepository(animationsRepository, animationUsersRepository);

        uniqueId = ThreadSafeRandom.randomUniqueId();
        animation = Mockito.mock(Animation.class);
    }

    @Test
    void notRegisteredThrow() {
        assertThrows(
                AnimationNotRegisteredException.class,
                () -> playerAnimationsRepository.addAvailableAnimation(uniqueId, animation)
        );
    }

    @Test
    void notAvailableThrow() {
        animationsRepository.storeEntity(animation);

        assertThrows(
                AnimationNotAvailableException.class,
                () -> playerAnimationsRepository.setSelectedAnimation(uniqueId, OBJECT, animation)
        );
    }
}
