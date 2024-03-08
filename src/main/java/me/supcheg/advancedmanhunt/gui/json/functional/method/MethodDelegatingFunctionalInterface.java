package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.common.logic.LogicDelegate;
import me.supcheg.advancedmanhunt.gui.impl.common.logic.LogicDelegatingAdvancedGui;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

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

    @SneakyThrows
    protected void accept(@NotNull AdvancedGui gui, @NotNull Object arg) {
        getLogicDelegate(gui).handle(handle, arg);
    }

    @NotNull
    protected LogicDelegate getLogicDelegate(@NotNull AdvancedGui gui) {
        if (!(gui instanceof LogicDelegatingAdvancedGui delegating)) {
            throw new IllegalStateException("%s is not instance of %s".formatted(
                    gui, LogicDelegatingAdvancedGui.class.getSimpleName())
            );
        }
        return delegating.getLogicDelegate();
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
