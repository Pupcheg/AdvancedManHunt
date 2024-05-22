package me.supcheg.advancedmanhunt.template;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.papermc.paper.math.Position;
import it.unimi.dsi.fastutil.Pair;
import me.supcheg.advancedmanhunt.action.ActionRunnable;
import me.supcheg.advancedmanhunt.math.PositionBoxIteratorSources;
import me.supcheg.advancedmanhunt.math.distance.Distance;
import me.supcheg.advancedmanhunt.math.distance.DistancePair;
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

import static me.supcheg.advancedmanhunt.math.PositionBox.box;
import static me.supcheg.advancedmanhunt.math.Positions.sameXZ;
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
    Set<Pair<Position, Position>> sourceToTarget;

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
                PositionBoxIteratorSources.XZ.stream(box(sameXZ(-2), sameXZ(2)))
                        .map(pos -> Path.of("r.%d.%d.mca".formatted(pos.blockX(), pos.blockZ())))
                        .collect(Collectors.toUnmodifiableSet())
        );
        template = templateMock;

        sourceToTarget = Collections.synchronizedSet(new HashSet<>());

        templateLoader = new AsyncTemplateLoader() {
            @NotNull
            @Override
            protected ActionRunnable createRunnable(@NotNull RegionLoadContext ctx) {
                return () -> sourceToTarget.add(Pair.of(
                        ctx.getOriginalPos(),
                        ctx.getTargetPos()
                ));
            }
        };

        region = new GameRegion(
                WorldReference.of(mock.addSimpleWorld("world")),
                DistancePair.ofRegionsSame(32),
                DistancePair.ofSame(MAX_REGION_SIDE_SIZE).addRegions(32, 32).subtractBlocks(1, 1)
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
