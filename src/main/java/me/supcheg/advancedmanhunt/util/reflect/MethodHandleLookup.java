package me.supcheg.advancedmanhunt.util.reflect;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.function.Supplier;

public interface MethodHandleLookup {
    @NotNull
    Supplier<MethodHandle> findMethod(@NotNull String name);
}
