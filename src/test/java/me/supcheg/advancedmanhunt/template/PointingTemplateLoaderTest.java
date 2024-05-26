package me.supcheg.advancedmanhunt.template;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.math.PositionBoxIteratorSources;
import me.supcheg.advancedmanhunt.math.distance.Distance;
import me.supcheg.advancedmanhunt.math.distance.DistancePair;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.structure.PointingTemplateLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;

import static me.supcheg.advancedmanhunt.math.PositionBox.box;
import static me.supcheg.advancedmanhunt.math.Positions.sameXZ;
import static me.supcheg.advancedmanhunt.mock.ServerMockBuilder.buildServerMock;
import static me.supcheg.advancedmanhunt.mock.ServerMockBuilder.getWorldContainerEmptyFile;
import static me.supcheg.advancedmanhunt.mock.WorldReferenceMock.mockWorldReference;
import static me.supcheg.advancedmanhunt.util.Keys.advancedmanhuntKey;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PointingTemplateLoaderTest {

    TemplateLoader templateLoader;
    Template template;
    GameRegion region;

    @BeforeEach
    void setup() {
        ServerMock mock = buildServerMock(
                getWorldContainerEmptyFile()
        );

        Path path = Path.of("");
        templateLoader = new PointingTemplateLoader(path);

        Template templateMock = mock(Template.class,
                delegatesTo(new Template(
                        advancedmanhuntKey("template"),
                        Distance.ofRegions(2),
                        path,
                        Collections.emptyList()
                ))
        );
        when(templateMock.getData()).thenReturn(
                PositionBoxIteratorSources.XZ.stream(box(sameXZ(-2), sameXZ(2)))
                        .map(pos -> Path.of("r.%d.%d.mca".formatted(pos.blockX(), pos.blockZ())))
                        .collect(Collectors.toUnmodifiableSet())
        );
        template = templateMock;

        region = new GameRegion(
                mockWorldReference(),
                DistancePair.ofRegionsSame(0),
                DistancePair.ofRegionsSame(33).subtractBlocks(1, 1)
        );
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Disabled("For visualization only")
    @Test
    void pointingTest() {
        templateLoader.loadTemplate(region, template);
    }
}
