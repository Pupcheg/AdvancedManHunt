package me.supcheg.advancedmanhunt.gui.impl.common.logic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;

public interface LogicDelegate {
    void handle(@NotNull MethodHandle handle, @Nullable Object @NotNull ... args);
}
