package me.supcheg.advancedmanhunt.timer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface CountDownTimer {

    @NotNull
    @Contract("-> this")
    CountDownTimer schedule();

    void cancel();
}
