package me.supcheg.advancedmanhunt.test.structure;

import be.seeseemelk.mockbukkit.MockBukkit;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.paper.PaperPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;

public class InjectingPaperPlugin extends PaperPlugin {

    public InjectingPaperPlugin() {
        super(DummyContainerAdapter.INSTANCE);
    }

    @NotNull
    @Contract(" -> new")
    public static InjectingPaperPlugin load() {
        return MockBukkit.loadSimple(InjectingPaperPlugin.class);
    }

    @SneakyThrows
    @NotNull
    @Contract(value = "_, _ -> this")
    public <T> InjectingPaperPlugin modifyField(@NotNull Class<T> fieldType, @Nullable T value) {
        Field field = Arrays.stream(PaperPlugin.class.getDeclaredFields())
                .filter(f -> f.getType().equals(fieldType))
                .findFirst()
                .orElseThrow();

        field.setAccessible(true);
        field.set(this, value);
        return this;
    }
}
