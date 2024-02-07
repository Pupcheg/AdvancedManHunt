package me.supcheg.advancedmanhunt.util.reflect;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

public class FutureMethodHandleLookup implements MethodHandleLookup {
    private final Collection<FutureMethodHandleSupplier> suppliers = new ArrayList<>();

    @NotNull
    @Override
    public Supplier<MethodHandle> findMethod(@NotNull String name) {
        FutureMethodHandleSupplier supplier = new FutureMethodHandleSupplier(name);
        suppliers.add(supplier);
        return supplier;
    }

    public void initializeAllWith(@NotNull MethodHandleLookup lookup) {
        for (FutureMethodHandleSupplier handle : suppliers) {
            handle.delegate = lookup.findMethod(handle.name);
            handle.name = null;
        }
    }

    private static final class FutureMethodHandleSupplier implements Supplier<MethodHandle> {
        private String name;
        private Supplier<MethodHandle> delegate;

        public FutureMethodHandleSupplier(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public MethodHandle get() {
            return Objects.requireNonNull(delegate, "delegate wasn't initialized").get();
        }
    }
}
