package me.supcheg.advancedmanhunt.action;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

        return CompletableFuture.supplyAsync(new ExecuteRunnable(executables)::run, defaultExecutor);
    }

    @NotNull
    @Contract("_, _ -> param2")
    private List<ExecutableAction> listExecutables(@NotNull Action action, @NotNull List<ExecutableAction> result) {
        if (action instanceof ExecutableAction executable) {
            result.add(executable);
        } else if (action instanceof JoinedAction joined) {
            joined.getActions().forEach(sub -> listExecutables(sub, result));
        }
        return result;
    }

    @RequiredArgsConstructor
    private class ExecuteRunnable {
        private final List<ExecutableAction> executables;
        private final List<Throwable> throwables = new LinkedList<>();

        @NotNull
        @Unmodifiable
        public List<Throwable> run() {
            for (ListIterator<ExecutableAction> it = executables.listIterator(); it.hasNext(); ) {
                ExecutableAction cursor = it.next();

                try {
                    apply(cursor, ExecutableAction::execute);
                } catch (Throwable thr) {
                    throwables.add(thr);

                    tryDiscard(cursor);
                    tryDiscardPrevious(it);
                    break;
                }
            }
            return Collections.unmodifiableList(throwables);
        }

        private void apply(@NotNull ExecutableAction action, @NotNull Consumer<ExecutableAction> consumer) throws Throwable {
            if (action.shouldRunOnMainThread()) {
                CompletableFuture.runAsync(() -> consumer.accept(action), mainThreadExecutor).get();
            } else {
                consumer.accept(action);
            }
        }

        private void tryDiscardPrevious(@NotNull ListIterator<ExecutableAction> it) {
            while (it.hasPrevious()) {
                tryDiscard(it.previous());
            }
        }

        private void tryDiscard(@NotNull ExecutableAction action) {
            try {
                apply(action, ExecutableAction::discard);
            } catch (ExecutionException ex) {
                throwables.add(ex.getCause() == null ? ex : ex.getCause());
            } catch (Throwable thr) {
                throwables.add(thr);
            }
        }
    }

}
