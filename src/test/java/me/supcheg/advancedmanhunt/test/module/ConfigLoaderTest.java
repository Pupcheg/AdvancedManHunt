package me.supcheg.advancedmanhunt.test.module;

import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.config.ConfigLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigLoaderTest {

    @Test
    void testPathResolver() throws NoSuchFieldException {
        String path = ConfigLoader.resolveConfigPath(AdvancedManHuntConfig.TemplateLoad.class,
                AdvancedManHuntConfig.TemplateLoad.class.getField("THREAD_POOL_SIZE"));
        Assertions.assertEquals("template_load.thread_pool_size", path);
    }

}
