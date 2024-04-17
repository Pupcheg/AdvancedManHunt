package me.supcheg.advancedmanhunt.template;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.structure.PointingTemplateLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;
import static me.supcheg.advancedmanhunt.coord.Coord.coord;
import static me.supcheg.advancedmanhunt.coord.Coords.iterateRangeInclusive;
import static me.supcheg.advancedmanhunt.util.Keys.advancedmanhuntKey;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PointingTemplateLoaderTest {

    private TemplateLoader templateLoader;
    private Template template;
    private GameRegion region;

    @BeforeEach
    void setup() {
        ServerMock mock = mock(ServerMock.class, delegatesTo(new ServerMock()));
        doReturn(new File("")).when(mock).getWorldContainer();
        MockBukkit.mock(mock);

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
                stream(spliteratorUnknownSize(iterateRangeInclusive(coord(-2, -2), coord(2, 2)), 0), false)
                        .map(coord -> Path.of("r.%d.%d.mca".formatted(coord.getX(), coord.getZ())))
                        .collect(Collectors.toUnmodifiableSet())
        );
        template = templateMock;

        region = new GameRegion(WorldReference.of(mock.addSimpleWorld("world")), Coord.coordSameXZ(0), Coord.coordSameXZ(32));
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
