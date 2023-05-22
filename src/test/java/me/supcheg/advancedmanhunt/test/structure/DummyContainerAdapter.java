package me.supcheg.advancedmanhunt.test.structure;

import me.supcheg.advancedmanhunt.region.ContainerAdapter;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public class DummyContainerAdapter implements ContainerAdapter {

    public static final ContainerAdapter INSTANCE = new DummyContainerAdapter();

    private DummyContainerAdapter() {
    }

    @Override
    @NotNull
    @Unmodifiable
    public List<String> getAllWorldNames() {
        return Collections.emptyList();
    }

    @Override
    public byte @Nullable [] read(@NotNull World world, @NotNull String fileName) {
        return null;
    }

    @Override
    public void write(@NotNull World world, @NotNull String fileName, byte @NotNull [] data) {
    }
}
