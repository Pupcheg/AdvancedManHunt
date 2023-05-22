package me.supcheg.advancedmanhunt.game;

import org.jetbrains.annotations.NotNull;

public enum GameState {

    CREATE,
    LOAD,
    START,
    PLAY,
    STOP,
    CLEAR,
    CLOSE,
    ERROR;

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
}
