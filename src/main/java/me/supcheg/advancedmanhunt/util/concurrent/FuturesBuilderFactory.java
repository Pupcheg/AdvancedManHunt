package me.supcheg.advancedmanhunt.util.concurrent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public interface FuturesBuilderFactory {
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    FuturesBuilder create(@NotNull Executor asyncExecutor);
}
