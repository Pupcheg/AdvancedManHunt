package me.supcheg.advancedmanhunt.bridge.impl.lazy;

import me.supcheg.advancedmanhunt.bridge.RegionPositionWriter;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsRegionPositionWriter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.nio.file.Path;

public class LazyRegionPositionWriter extends LazyBridge<RegionPositionWriter>
        implements RegionPositionWriter {
    @Inject
    public LazyRegionPositionWriter() {
        super(RegionPositionWriter.class,
                NmsRegionPositionWriter::new
        );
    }

    @Override
    public void writePositionsToRegion(@NotNull Path regionPath) {
        delegate().writePositionsToRegion(regionPath);
    }
}
