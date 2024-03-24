package me.supcheg.advancedmanhunt.action;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

@CustomLog
@RequiredArgsConstructor
public class DefaultActionExecutor implements ActionExecutor {
    private final Executor selfExecutor = Executors.newFixedThreadPool(4);
    private final Executor mainThreadExecutor;
    private final Executor anyThreadExecutor;

    @NotNull
    @Override
    public RunningAction execute(@NotNull Action root) {
        List<ExecutableAction> executables = listExecutables(root, new LinkedList<>());
        if (executables.isEmpty()) {
            throw new IllegalArgumentException("No ExecutableAction in " + root);
        }

        ExecuteRunnable executeRunnable = new ExecuteRunnable(executables);
        executeRunnable.execute();
        return executeRunnable;
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

    private enum InterruptType {
        INTERRUPT, INTERRUPT_AND_DISCARD
    }

    @RequiredArgsConstructor
    private class ExecuteRunnable implements RunningAction {
        private final List<ExecutableAction> executables;
        private final List<ActionThrowable> throwables = Collections.synchronizedList(new LinkedList<>());
        private final AtomicReference<InterruptType> interrupt = new AtomicReference<>();

        private CompletableFuture<RunningAction> selfFuture;

        @NotNull
        @Override
        public CompletableFuture<RunningAction> asCompletableFuture() {
            return selfFuture;
        }

        @NotNull
        @Override
        public List<ActionThrowable> listThrowables() {
            return List.copyOf(throwables);
        }

        @Override
        public boolean isInterrupted() {
            return interrupt.get() != null;
        }

        @Override
        public void interruptWithoutDiscard() {
            interrupt.set(InterruptType.INTERRUPT);
        }

        @Override
        public void interruptAndDiscard() {
            interrupt.set(InterruptType.INTERRUPT_AND_DISCARD);
        }

        public void execute() {
            Supplier<RunningAction> supplier = () -> {
                for (ListIterator<ExecutableAction> it = executables.listIterator(); it.hasNext(); ) {

                    InterruptType interruptType = interrupt.get();
                    if (interruptType != null) {

                        if (interruptType == InterruptType.INTERRUPT_AND_DISCARD) {
                            tryDiscardPrevious(it);
                        }

                        return this;
                    }

                    ExecutableAction cursor = it.next();

                    try {
                        apply(cursor, ExecutableAction::execute).get();
                    } catch (Throwable thr) {
                        throwables.add(new ActionThrowable(cursor, thr));

                        tryDiscard(cursor);
                        tryDiscardPrevious(it);
                        break;
                    }
                }
                return ExecuteRunnable.this;
            };

            selfFuture = CompletableFuture.supplyAsync(supplier, selfExecutor);
        }

        @NotNull
        @Contract("_, _ -> new")
        private CompletableFuture<Void> apply(@NotNull ExecutableAction action, @NotNull Consumer<ExecutableAction> consumer) {
            log.debugIfEnabled("Applying '{}' to '{}'", consumer, action.name());

            return CompletableFuture.runAsync(
                    () -> consumer.accept(action),
                    action.shouldRunOnMainThread() ? mainThreadExecutor : anyThreadExecutor
            );
        }

        private void tryDiscardPrevious(@NotNull ListIterator<ExecutableAction> it) {
            while (it.hasPrevious()) {
                tryDiscard(it.previous());
            }
        }

        private void tryDiscard(@NotNull ExecutableAction action) {
            try {
                apply(action, ExecutableAction::discard).get();
            } catch (Throwable thr) {
                throwables.add(new ActionThrowable(action, thr));
            }
        }
    }

}
