package me.supcheg.advancedmanhunt.util;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class Unchecked {

    private Unchecked() {
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Contract("_ -> param1")
    public static <T> T uncheckedCast(@Nullable Object o) {
        return (T) o;
    }

    @Nullable
    @Contract("_ -> param1")
    public static <T, R> Function<T, R> uncheckedFunction(@Nullable ThrFunction<T, R> function) {
        return function;
    }

    public interface ThrFunction<T, R> extends Function<T, R> {

        R applyThr(T t) throws Throwable;

        @SneakyThrows
        @Override
        default R apply(T t) {
            return applyThr(t);
        }
    }
}
