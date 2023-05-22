package me.supcheg.advancedmanhunt.timer.impl;

import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.timer.EveryPeriodConsumer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@AllArgsConstructor
public class DefaultCountDownTimerFactory implements CountDownTimerFactory {
    private final Plugin plugin;

    @NotNull
    public CountDownTimer newTimer(@NotNull EveryPeriodConsumer everyPeriod,
                                   @NotNull Consumer<CountDownTimer> afterComplete,
                                   long periodSeconds, int times) {
        return new DefaultCountDownTimer(plugin, everyPeriod, afterComplete, periodSeconds, times);
    }
}
