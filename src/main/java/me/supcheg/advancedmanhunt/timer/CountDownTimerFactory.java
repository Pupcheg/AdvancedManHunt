package me.supcheg.advancedmanhunt.timer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface CountDownTimerFactory {
    @NotNull
    @Contract("_, _, _, _ -> new")
    CountDownTimer newTimer(@NotNull EveryPeriodConsumer everyPeriod,
                            @NotNull Consumer<CountDownTimer> afterComplete,
                            long periodSeconds, int times);

    @NotNull
    @Contract("-> new")
    CountDownTimerBuilder newBuilder();
}
