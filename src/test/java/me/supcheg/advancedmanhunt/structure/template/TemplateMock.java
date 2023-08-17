package me.supcheg.advancedmanhunt.structure.template;

import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.Template;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public class TemplateMock extends Template {

    public TemplateMock(@NotNull String name, @NotNull Path path) {
        super(name, Distance.ofChunks(0), path, Collections.emptyList());
    }

    public TemplateMock(@NotNull String name) {
        this(name, Path.of(""));
    }

    @NotNull
    @Override
    public Set<Path> getData() {
        return Collections.emptySet();
    }
}
