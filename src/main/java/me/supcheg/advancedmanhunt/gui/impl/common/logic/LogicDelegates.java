package me.supcheg.advancedmanhunt.gui.impl.common.logic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogicDelegates {
    @NotNull
    public static LogicDelegate staticDelegate() {
        return MethodHandle::invoke;
    }

    @NotNull
    public static LogicDelegate nonStaticDelegate(@NotNull Object self) {
        Objects.requireNonNull(self, "self");
        return (handle, arg) -> handle.invoke(self, arg);
    }
}
