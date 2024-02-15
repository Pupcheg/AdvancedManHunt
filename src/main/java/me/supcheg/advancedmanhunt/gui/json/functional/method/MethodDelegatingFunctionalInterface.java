package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.json.LogicDelegatingAdvancedGui;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class MethodDelegatingFunctionalInterface {
    public static final String INSTANCE = "logic_inst";

    protected final String serialized;
    protected final MethodHandle handle;

    @NotNull
    public String getSerialized() {
        return serialized;
    }

    @NotNull
    public MethodHandle getHandle() {
        return handle;
    }

    @NotNull
    protected Object getLogicInstance(@NotNull AdvancedGui gui) {
        if (!(gui instanceof LogicDelegatingAdvancedGui delegating)) {
            throw new IllegalStateException("%s is not instance of %s".formatted(
                    gui, LogicDelegatingAdvancedGui.class.getSimpleName())
            );
        }
        return delegating.getLogicInstance();
    }

    @Override
    public int hashCode() {
        return serialized.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(methodName=" + serialized + ')';
    }
}
