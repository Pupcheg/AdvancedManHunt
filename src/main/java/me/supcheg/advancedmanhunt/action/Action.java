package me.supcheg.advancedmanhunt.action;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public sealed interface Action permits ExecutableAction, JoinedAction {
    @NotNull
    @Contract("_ -> new")
    static JoinedAction join(@NotNull List<ExecutableAction> executableActions) {
        return new PlainJoinedAction(List.copyOf(executableActions));
    }

    @NotNull
    @Contract("_ -> new")
    static JoinedAction join(@NotNull Action @NotNull ... actions) {
        return new PlainJoinedAction(List.of(actions));
    }

    @NotNull
    @Contract("-> new")
    static JoinedAction join() {
        return new PlainJoinedAction(Collections.emptyList());
    }

    @NotNull
    @Contract("_ -> new")
    static JoinedAction join(@NotNull ExecutableActionBuilder @NotNull ... builders) {
        return new PlainJoinedAction(Arrays.stream(builders).map(ExecutableActionBuilder::build).toList());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    static ExecutableActionBuilder mainThread(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        return new PlainExecutableAction.Builder(true, name);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    static ExecutableActionBuilder anyThread(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        return new PlainExecutableAction.Builder(false, name);
    }
}
