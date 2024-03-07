package me.supcheg.advancedmanhunt.gui.impl.common.logic;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;

@RequiredArgsConstructor
public class DefaultLogicDelegate implements LogicDelegate {
    private final Object object;

    @SneakyThrows
    @Override
    public void handle(@NotNull MethodHandle handle, @Nullable Object @NotNull ... args) {
        handle.invokeWithArguments(addObjectTo(args));
    }

    @Nullable
    private Object @NotNull [] addObjectTo(@Nullable Object @NotNull [] args) {
        return switch (args.length) {
            case 0 -> new Object[]{object};
            case 1 -> new Object[]{object, args[0]};
            default -> {
                Object[] newArgs = new Object[args.length + 1];
                newArgs[0] = object;
                System.arraycopy(args, 0, newArgs, 1, newArgs.length);
                yield newArgs;
            }
        };
    }
}
