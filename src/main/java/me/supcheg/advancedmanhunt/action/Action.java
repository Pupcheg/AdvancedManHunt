package me.supcheg.advancedmanhunt.action;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Actions API:
 * <tr>- All actions will be performed in the specified order
 * <tr>- You can combine tasks in the main thread with asynchronous ones
 * <tr>- If a task throws an exception, it will be discarded, like all previous ones
 * @see ExecutableAction
 * @see ExecutableActionBuilder
 * @see JoinedAction
 * @see ActionExecutor
 * @see DefaultActionExecutor
 */
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
