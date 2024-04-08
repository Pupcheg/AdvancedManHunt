package me.supcheg.advancedmanhunt.animation;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.UUID;

public interface PlayerAnimationsRepository {
    @Nullable
    Animation getSelectedAnimation(@NotNull UUID uniqueId, @NotNull Key object);

    void setSelectedAnimation(@NotNull UUID uniqueId, @NotNull Key object, @NotNull Animation animation);

    @NotNull
    @Unmodifiable
    Collection<Animation> getAvailableAnimations(@NotNull UUID uniqueId);

    void addAvailableAnimation(@NotNull UUID uniqueId, @NotNull Animation animation);

    void removeAvailableAnimation(@NotNull UUID uniqueId, @NotNull Animation animation);
}
