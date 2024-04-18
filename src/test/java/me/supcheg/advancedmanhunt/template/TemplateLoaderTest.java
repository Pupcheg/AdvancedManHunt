package me.supcheg.advancedmanhunt.template;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import it.unimi.dsi.fastutil.Pair;
import me.supcheg.advancedmanhunt.action.ActionRunnable;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.template.impl.AsyncTemplateLoader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;
import static me.supcheg.advancedmanhunt.coord.Coord.coord;
import static me.supcheg.advancedmanhunt.coord.Coord.coordSameXZ;
import static me.supcheg.advancedmanhunt.coord.Coords.iterateRangeInclusive;
import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_SIDE_SIZE;
import static me.supcheg.advancedmanhunt.util.Keys.advancedmanhuntKey;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TemplateLoaderTest {
    TemplateLoader templateLoader;
    Template template;
    GameRegion region;
    Set<Pair<Coord, Coord>> sourceToTarget;

    @BeforeEach
    void setup() {
        ServerMock mock = mock(ServerMock.class, delegatesTo(new ServerMock()));
        doReturn(new File("")).when(mock).getWorldContainer();
        MockBukkit.mock(mock);

        Template templateMock = mock(Template.class,
                delegatesTo(new Template(
                        advancedmanhuntKey("template"),
                        Distance.ofRegions(2),
                        Path.of(""),
                        Collections.emptyList()
                ))
        );
        when(templateMock.getData()).thenReturn(
                stream(spliteratorUnknownSize(iterateRangeInclusive(coord(-2, -2), coord(2, 2)), 0), false)
                        .map(coord -> Path.of("r.%d.%d.mca".formatted(coord.getX(), coord.getZ())))
                        .collect(Collectors.toUnmodifiableSet())
        );
        template = templateMock;

        sourceToTarget = new HashSet<>();

        templateLoader = new AsyncTemplateLoader() {
            @NotNull
            @Override
            protected ActionRunnable createRunnable(@NotNull RegionLoadContext ctx) {
                return () -> sourceToTarget.add(Pair.of(
                        ctx.getOriginalCoord(),
                        ctx.getTargetCoord()
                ));
            }
        };

        region = new GameRegion(
                WorldReference.of(mock.addSimpleWorld("world")),
                coordSameXZ(32),
                coordSameXZ(32 + MAX_REGION_SIDE_SIZE.getRegions())
        );
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void countTemplatesTest() {
        templateLoader.loadTemplate(region, template);

        int sideSize = template.getSideSize().getRegions();
        assertSame(sideSize * sideSize, sourceToTarget.size());
    }
}
