package me.supcheg.advancedmanhunt.timer;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

@CanIgnoreReturnValue
public interface CountDownTimerBuilder {

    @NotNull
    @Contract("_ -> this")
    CountDownTimerBuilder everyPeriod(@NotNull EveryPeriodConsumer everyPeriod);

    @NotNull
    @Contract("_ -> this")
    default CountDownTimerBuilder everyPeriod(@NotNull LongConsumer everyPeriod) {
        return everyPeriod((__, l) -> everyPeriod.accept(l));
    }

    @NotNull
    @Contract("_ -> this")
    CountDownTimerBuilder afterComplete(@NotNull Consumer<CountDownTimer> afterComplete);

    @NotNull
    @Contract("_ -> this")
    default CountDownTimerBuilder afterComplete(@NotNull Runnable afterComplete) {
        return afterComplete(__ -> afterComplete.run());
    }

    @NotNull
    @Contract("_ -> this")
    CountDownTimerBuilder period(long periodSeconds);

    @NotNull
    @Contract("_ -> this")
    CountDownTimerBuilder times(long times);

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
