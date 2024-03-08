package me.supcheg.advancedmanhunt.gui.impl.common.logic;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

public interface LogicDelegate {
    void handle(@NotNull MethodHandle handle, @NotNull Object arg) throws Throwable;
}
