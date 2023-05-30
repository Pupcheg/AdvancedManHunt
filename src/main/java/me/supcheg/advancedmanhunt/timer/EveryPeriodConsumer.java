package me.supcheg.advancedmanhunt.timer;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EveryPeriodConsumer {
    void accept(@NotNull CountDownTimer timer, long leftTimes);
}
