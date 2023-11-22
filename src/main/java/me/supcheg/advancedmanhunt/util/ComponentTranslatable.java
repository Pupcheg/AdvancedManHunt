package me.supcheg.advancedmanhunt.util;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;

public interface ComponentTranslatable extends Translatable, ComponentLike {
    @NotNull
    TranslatableComponent asComponent();

    @NotNull
    @Override
    default String translationKey() {
        return asComponent().key();
    }
}
