package me.supcheg.advancedmanhunt.action;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActionRunnables {
    private static final ActionRunnable NOTHING = () -> {/* nothing */};

    @NotNull
    public static ActionRunnable doNothing() {
        return NOTHING;
    }
}
