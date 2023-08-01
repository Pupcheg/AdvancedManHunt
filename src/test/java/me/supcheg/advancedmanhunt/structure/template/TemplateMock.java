package me.supcheg.advancedmanhunt.structure.template;

import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.Template;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public class TemplateMock extends Template {
    public TemplateMock(@NotNull String name) {
        super(name, Distance.ofChunks(512), Path.of(""), Collections.emptyList());
    }

    @NotNull
    @Override
    public Set<Path> getData() {
        return Collections.emptySet();
    }
}
