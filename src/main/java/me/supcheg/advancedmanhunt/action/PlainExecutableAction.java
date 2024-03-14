package me.supcheg.advancedmanhunt.action;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

final class PlainExecutableAction implements ExecutableAction {
    private final boolean mainThread;
    private final String name;
    private final ActionRunnable execute;
    private final ActionRunnable discard;

    PlainExecutableAction(@NotNull Builder builder) {
        this.mainThread = builder.mainThread;
        this.name = builder.name;
        this.execute = builder.execute;
        this.discard = builder.discard;
    }

    @NotNull
    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean shouldRunOnMainThread() {
        return mainThread;
    }

    @SneakyThrows
    @Override
    public void execute() {
        execute.run();
    }

    @SneakyThrows
    @Override
    public void discard() {
        discard.run();
    }

    @RequiredArgsConstructor
    static final class Builder implements ExecutableActionBuilder {
        private final boolean mainThread;
        private final String name;
        private ActionRunnable execute = ActionRunnables.doNothing();
        private ActionRunnable discard = ActionRunnables.doNothing();

        @NotNull
        @Override
        public ExecutableActionBuilder execute(@NotNull ActionRunnable execute) {
            Objects.requireNonNull(execute, "execute");
            this.execute = execute;
            return this;
        }

        @NotNull
        @Override
        public ExecutableActionBuilder discard(@NotNull ActionRunnable discard) {
            Objects.requireNonNull(discard, "discard");
            this.discard = discard;
            return this;
        }

        @NotNull
        @Override
        public ExecutableAction build() {
            return new PlainExecutableAction(this);
        }
    }
}
