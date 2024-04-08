package me.supcheg.advancedmanhunt.timer;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public sealed interface CountDownTimer permits DefaultCountDownTimer {

    @NotNull
    @Contract("_ -> new")
    static CountDownTimerBuilder times(int times) {
        return new DefaultCountDownTimer.Builder(times);
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("-> this")
    CountDownTimer schedule();

    boolean isRunning();

    boolean isOver();

    void cancel();
}
