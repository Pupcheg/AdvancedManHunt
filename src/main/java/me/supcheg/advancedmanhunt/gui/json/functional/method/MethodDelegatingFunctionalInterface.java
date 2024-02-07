package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class MethodDelegatingFunctionalInterface {
    private final String methodName;
    private final Supplier<MethodHandle> handle;

    @NotNull
    public String getMethodName() {
        return methodName;
    }

    @NotNull
    public MethodHandle getHandle() {
        return handle.get();
    }
}
