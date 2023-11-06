package me.supcheg.advancedmanhunt.game;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.util.ComponentTranslatable;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

@RequiredArgsConstructor
public enum GameState implements ComponentTranslatable {
    CREATE(translatable("advancedmanhunt.game.state.create", NamedTextColor.GOLD)),
    LOAD(translatable("advancedmanhunt.game.state.load", NamedTextColor.GOLD)),
    START(translatable("advancedmanhunt.game.state.start", NamedTextColor.YELLOW)),
    PLAY(translatable("advancedmanhunt.game.state.play", NamedTextColor.GREEN)),
    STOP(translatable("advancedmanhunt.game.state.stop", NamedTextColor.RED)),
    CLEAR(translatable("advancedmanhunt.game.state.clear", NamedTextColor.AQUA)),
    END(translatable("advancedmanhunt.game.state.end", NamedTextColor.GOLD)),
    ERROR(translatable("advancedmanhunt.game.state.error", NamedTextColor.RED));

    private final TranslatableComponent component;

    public boolean upperOrEquals(@NotNull GameState other) {
        return ordinal() >= other.ordinal();
    }

    public boolean upper(@NotNull GameState other) {
        return ordinal() > other.ordinal();
    }

    public boolean lowerOrEquals(@NotNull GameState other) {
        return ordinal() <= other.ordinal();
    }

    public boolean lower(@NotNull GameState other) {
        return ordinal() < other.ordinal();
    }

    public void assertIs(@NotNull GameState other) {
        if (this != other) {
            throw new IllegalStateException(this + " != " + other);
        }
    }

    @NotNull
    @Override
    public TranslatableComponent asComponent() {
        return component;
    }
}
