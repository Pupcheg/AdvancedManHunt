package me.supcheg.advancedmanhunt.util.reflect;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.function.Supplier;

public class ExceptionallyMethodHandleLookup implements MethodHandleLookup {
    @NotNull
    @Override
    public Supplier<MethodHandle> findMethod(@NotNull String name) {
        throw new UnsupportedOperationException("#findMethod(String)");
    }
}
