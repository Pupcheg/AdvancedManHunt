package me.supcheg.advancedmanhunt.action;

import org.jetbrains.annotations.NotNull;

public sealed interface ExecutableAction extends Action permits PlainExecutableAction {
    @NotNull
    String name();

    boolean shouldRunOnMainThread();

    void execute();

    void discard();
}
