package me.supcheg.advancedmanhunt.util.reflect;

import com.google.common.base.Suppliers;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static java.util.function.Predicate.not;

@CustomLog
@RequiredArgsConstructor
public class InstantMethodHandleLookup implements MethodHandleLookup {
    private final Object obj;
    private final Collection<String> usedMethods = new ArrayList<>();

    @NotNull
    @Override
    public Supplier<MethodHandle> findMethod(@NotNull String name) {
        return Suppliers.ofInstance(
                Arrays.stream(obj.getClass().getDeclaredMethods())
                        .filter(m -> m.getName().equals(name))
                        .peek(Method::trySetAccessible)
                        .peek(this::warnIfNoAnnotation)
                        .peek(method -> usedMethods.add(method.getName()))
                        .findFirst()
                        .map(this::unreflectMethod)
                        .map(handle -> handle.bindTo(obj))
                        .orElseThrow()
        );
    }

    @NotNull
    @Unmodifiable
    @Contract("-> new")
    public List<String> findUnusedMethods() {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(ReflectCalled.class))
                .map(Method::getName)
                .filter(not(usedMethods::contains))
                .toList();
    }

    public void logIfHasUnusedMethods() {
        List<String> unused = findUnusedMethods();
        if (!unused.isEmpty()) {
            log.warn(
                    "{} has unused methods, marked with {} annotation. {}",
                    obj.getClass().getSimpleName(), ReflectCalled.class.getSimpleName(), String.join(", ", unused)
            );
        }
    }

    @SneakyThrows
    @NotNull
    private MethodHandle unreflectMethod(@NotNull Method method) {
        return MethodHandles.lookup().unreflect(method);
    }

    private void warnIfNoAnnotation(@NotNull Method method) {
        if (!method.isAnnotationPresent(ReflectCalled.class)) {
            log.warn(
                    "Method {} is used by {}, but isn't marked with {} annotation",
                    method, MethodHandleLookup.class.getSimpleName(), ReflectCalled.class.getSimpleName()
            );
        }
    }
}
