package me.supcheg.advancedmanhunt.template;

import me.supcheg.advancedmanhunt.template.impl.ChunkCoordReplacer;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class ChunkCoordReplacerTest {

    private static final Path SOURCE_FILE = Path.of("C:/Users/Supcheg/Desktop/r.0.0.mca");
    private static final Path TARGET_FILE = Path.of("C:/Users/Supcheg/Desktop/r.10.10.mca");

    @Test
    void noExceptionTest() throws Exception {
        for (int i = 0; i < 20; i++) {
            try (ChunkCoordReplacer replacer = new ChunkCoordReplacer(SOURCE_FILE, TARGET_FILE)) {
                replacer.deserializeRegion();
                replacer.serializeRegion(10, 10);
            }
            System.out.println(i);
        }

        long start = System.currentTimeMillis();
        try (ChunkCoordReplacer replacer = new ChunkCoordReplacer(SOURCE_FILE, TARGET_FILE)) {
            replacer.deserializeRegion();
            replacer.serializeRegion(10, 10);
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }
}
