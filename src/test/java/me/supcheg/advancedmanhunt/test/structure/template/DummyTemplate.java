package me.supcheg.advancedmanhunt.test.structure.template;

import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.Template;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public class DummyTemplate extends Template {

    public static final Template INSTANCE = new DummyTemplate();

    private DummyTemplate() {
        super("dummyTemplate", Distance.ofChunks(512), Path.of(""));
    }

    @NotNull
    @Override
    public Set<Path> getData() {
        return Collections.emptySet();
    }
}
