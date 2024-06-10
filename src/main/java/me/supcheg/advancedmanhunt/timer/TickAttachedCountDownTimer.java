package me.supcheg.advancedmanhunt.timer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.platform.paper.PluginUtil;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

final class TickAttachedCountDownTimer implements CountDownTimer {
    private final EveryPeriodConsumer everyPeriod;
    private final Consumer<CountDownTimer> afterComplete;
    private final long periodSeconds;
    private final long times;

    private BukkitTask bukkitTask;
    private long leftTimes;

    TickAttachedCountDownTimer(@NotNull Builder builder) {
        this.everyPeriod = builder.everyPeriod;
        this.afterComplete = builder.afterComplete;
        this.periodSeconds = builder.periodSeconds;
        this.times = builder.times;
    }

    @NotNull
    @Override
    public TickAttachedCountDownTimer schedule() {
        if (bukkitTask != null) {
            throw new IllegalStateException("This timer has already been scheduled");
        }
        leftTimes = times;
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (--leftTimes <= 0) {
                    afterComplete.accept(TickAttachedCountDownTimer.this);
                    cancel();
                } else {
                    everyPeriod.accept(TickAttachedCountDownTimer.this, leftTimes);
                }
            }
        }.runTaskTimer(PluginUtil.getPlugin(), 0, periodSeconds * 20);

        return this;
    }

    @Override
    public boolean isRunning() {
        return bukkitTask != null && !bukkitTask.isCancelled() && leftTimes > 0;
    }

    @Override
    public boolean isOver() {
        return leftTimes <= 0 || (bukkitTask != null && bukkitTask.isCancelled());
    }

    @Override
    public void cancel() {
        if (bukkitTask != null && !bukkitTask.isCancelled()) {
            bukkitTask.cancel();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TickAttachedCountDownTimer that)) {
            return false;
        }

        return periodSeconds == that.periodSeconds
                && times == that.times
                && everyPeriod.equals(that.everyPeriod)
                && afterComplete.equals(that.afterComplete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                everyPeriod,
                afterComplete,
                periodSeconds,
                times
        );
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    static final class Builder implements CountDownTimerBuilder {
        private EveryPeriodConsumer everyPeriod;
        private Consumer<CountDownTimer> afterComplete;
        private long periodSeconds = 1;
        private final long times;

        @NotNull
        @Override
        public CountDownTimerBuilder everyPeriod(@NotNull EveryPeriodConsumer everyPeriod) {
            Objects.requireNonNull(everyPeriod);
            this.everyPeriod = everyPeriod;
            return this;
        }

        @NotNull
        @Override
        public CountDownTimerBuilder afterComplete(@NotNull Consumer<CountDownTimer> afterComplete) {
            Objects.requireNonNull(afterComplete);
            this.afterComplete = afterComplete;
            return this;
        }

        @NotNull
        @Override
        public CountDownTimerBuilder period(long periodSeconds) {
            this.periodSeconds = periodSeconds;
            return this;
        }

        @NotNull
        @Override
        public CountDownTimer build() {
            return new TickAttachedCountDownTimer(this);
        }
    }
}
