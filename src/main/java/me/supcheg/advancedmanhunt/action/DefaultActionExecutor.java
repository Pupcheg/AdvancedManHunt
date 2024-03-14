package me.supcheg.advancedmanhunt.action;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class DefaultActionExecutor implements ActionExecutor {
    private final Executor mainThreadExecutor;
    private final Executor defaultExecutor;

    @NotNull
    @Override
    public CompletableFuture<List<Throwable>> execute(@NotNull Action root) {
        List<ExecutableAction> executables = listExecutables(root, new LinkedList<>());
        if (executables.isEmpty()) {
            throw new IllegalArgumentException("No ExecutableAction in " + root);
        }


        ListIterator<ExecutableAction> it = executables.listIterator();
        return CompletableFuture.supplyAsync(() -> execute(it), defaultExecutor);
    }

    @NotNull
    @Unmodifiable
    private List<Throwable> execute(@NotNull ListIterator<ExecutableAction> it) {
        List<Throwable> throwables = new LinkedList<>();
        while (it.hasNext()) {
            ExecutableAction cursor = it.next();

            try {
                if (cursor.shouldRunOnMainThread()) {
                    CompletableFuture.runAsync(cursor::execute, mainThreadExecutor).get();
                } else {
                    cursor.execute();
                }
            } catch (Throwable e) {
                throwables.add(e);

                tryDiscard(cursor, throwables::add);
                while (it.hasPrevious()) {
                    tryDiscard(it.previous(), throwables::add);
                }

                break;
            }
        }

        return Collections.unmodifiableList(throwables);
    }

    private void tryDiscard(@NotNull ExecutableAction action, @NotNull Consumer<Throwable> consumer) {
        try {
            if (action.shouldRunOnMainThread()) {
                CompletableFuture.runAsync(action::discard, mainThreadExecutor).get();
            } else {
                action.discard();
            }
        } catch (Throwable e) {
            consumer.accept(e);
        }
    }

    @NotNull
    private List<ExecutableAction> listExecutables(@NotNull Action action, List<ExecutableAction> result) {
        if (action instanceof ExecutableAction executable) {
            result.add(executable);
        } else if (action instanceof JoinedAction joined) {
            joined.getActions().forEach(sub -> listExecutables(sub, result));
        }
        return result;
    }

}
