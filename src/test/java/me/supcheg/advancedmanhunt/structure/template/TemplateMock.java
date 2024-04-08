package me.supcheg.advancedmanhunt.structure.template;

import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.Template;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public class TemplateMock extends Template {

    public TemplateMock(@NotNull Key key, @NotNull Path path) {
        super(key, Distance.ofChunks(0), path, Collections.emptyList());
    }

    public TemplateMock(@NotNull Key key) {
        this(key, Path.of(""));
    }

    @NotNull
    @Override
    public Set<Path> getData() {
        return Collections.emptySet();
    }
}
