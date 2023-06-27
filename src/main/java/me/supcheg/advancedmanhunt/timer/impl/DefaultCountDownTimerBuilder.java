package me.supcheg.advancedmanhunt.timer.impl;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.timer.CountDownTimerBuilder;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.timer.EveryPeriodConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

@RequiredArgsConstructor
class DefaultCountDownTimerBuilder implements CountDownTimerBuilder {
    private final CountDownTimerFactory factory;
    private EveryPeriodConsumer everyPeriod;
    private Consumer<CountDownTimer> afterComplete;
    private long periodSeconds = 1;
    private long times;
    private Collection<Consumer<CountDownTimer>> onBuild;

    @NotNull
    @Contract("_ -> this")
    @Override
    public CountDownTimerBuilder everyPeriod(@NotNull EveryPeriodConsumer everyPeriod) {
        Objects.requireNonNull(everyPeriod);
        this.everyPeriod = everyPeriod;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public CountDownTimerBuilder afterComplete(@NotNull Consumer<CountDownTimer> afterComplete) {
        Objects.requireNonNull(afterComplete);
        this.afterComplete = afterComplete;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public CountDownTimerBuilder period(long periodSeconds) {
        this.periodSeconds = periodSeconds;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public CountDownTimerBuilder times(long times) {
        this.times = times;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public CountDownTimerBuilder onBuild(@NotNull Consumer<CountDownTimer> consumer) {
        if (onBuild == null) {
            onBuild = new ArrayList<>();
        }
        onBuild.add(consumer);

        return this;
    }

    @NotNull
    @Contract("-> new")
    @Override
    public CountDownTimer build() {
        CountDownTimer timer = factory.newTimer(everyPeriod, afterComplete, periodSeconds, times);
        if (onBuild != null) {
            for (Consumer<CountDownTimer> consumer : onBuild) {
                consumer.accept(timer);
            }
        }
        return timer;
    }
}
