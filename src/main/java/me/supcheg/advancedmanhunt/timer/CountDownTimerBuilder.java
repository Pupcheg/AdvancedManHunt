package me.supcheg.advancedmanhunt.timer;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface CountDownTimerBuilder {

    @NotNull
    @Contract("_ -> this")
    CountDownTimerBuilder everyPeriod(EveryPeriodConsumer everyPeriod);

    @NotNull
    @Contract("_ -> this")
    CountDownTimerBuilder afterComplete(Consumer<CountDownTimer> afterComplete);

    @NotNull
    @Contract("_ -> this")
    CountDownTimerBuilder period(long periodSeconds);

    @NotNull
    @Contract("_ -> this")
    CountDownTimerBuilder times(int times);

    @NotNull
    @Contract("_ -> this")
    CountDownTimerBuilder onBuild(@NotNull Consumer<CountDownTimer> consumer);

    @NotNull
    @Contract("-> new")
    CountDownTimer build();

    @NotNull
    @Contract("-> new")
    @CanIgnoreReturnValue
    default CountDownTimer schedule() {
        return build().schedule();
    }
}
