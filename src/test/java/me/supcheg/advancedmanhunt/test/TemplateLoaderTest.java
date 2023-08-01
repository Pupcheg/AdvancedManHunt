package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.structure.PointingTemplateLoader;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;

class TemplateLoaderTest {

    private TemplateLoader templateLoader;
    private Template template;
    private GameRegion region;

    @BeforeEach
    void setup() {
        ServerMock mock = MockBukkit.mock();
        Path path = Path.of("");
        templateLoader = new PointingTemplateLoader(path);
        template = new Template("template", Distance.ofRegions(16), path, Collections.emptyList());
        region = new GameRegion(WorldReference.of(mock.addSimpleWorld("world")), KeyedCoord.of(0), KeyedCoord.of(32));
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Disabled("For visualizing only")
    @Test
    void pointingTest() {
        templateLoader.loadTemplate(region, template);
    }
}
