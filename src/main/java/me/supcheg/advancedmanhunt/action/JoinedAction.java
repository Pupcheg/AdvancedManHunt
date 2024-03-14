package me.supcheg.advancedmanhunt.action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public sealed interface JoinedAction extends Action permits PlainJoinedAction {
    @NotNull
    @Unmodifiable
    List<? extends Action> getActions();
}
