package me.supcheg.advancedmanhunt.timer;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface CountDownTimerFactory {
    @NotNull
    CountDownTimer newTimer(@NotNull EveryPeriodConsumer everyPeriod,
                            @NotNull Consumer<CountDownTimer> afterComplete,
                            long periodSeconds, int times);

}
