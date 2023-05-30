package me.supcheg.advancedmanhunt.timer.impl;

import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.timer.EveryPeriodConsumer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

class DefaultCountDownTimer implements CountDownTimer {
    private final Plugin plugin;
    private final EveryPeriodConsumer everyPeriod;
    private final Consumer<CountDownTimer> afterComplete;
    private final long periodSeconds;
    private final long times;

    private BukkitTask bukkitTask;
    private long leftTimes;

    DefaultCountDownTimer(@NotNull Plugin plugin, @NotNull EveryPeriodConsumer everyPeriod,
                          @NotNull Consumer<CountDownTimer> afterComplete,
                          long periodSeconds, long times) {
        this.plugin = plugin;
        this.everyPeriod = everyPeriod;
        this.afterComplete = afterComplete;
        this.periodSeconds = periodSeconds;
        this.times = times;
    }

    @NotNull
    @Contract("-> this")
    @Override
    public DefaultCountDownTimer schedule() {
        if (bukkitTask != null) {
            throw new IllegalStateException("This timer has already been scheduled");
        }
        leftTimes = times;
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (--leftTimes <= 0) {
                    afterComplete.accept(DefaultCountDownTimer.this);
                    cancel();
                } else {
                    everyPeriod.accept(DefaultCountDownTimer.this, leftTimes);
                }
            }
        }.runTaskTimer(plugin, 0, periodSeconds * 20);

        return this;
    }

    @Override
    public void cancel() {
        if (bukkitTask != null && !bukkitTask.isCancelled()) {
            bukkitTask.cancel();
        }
    }
}
