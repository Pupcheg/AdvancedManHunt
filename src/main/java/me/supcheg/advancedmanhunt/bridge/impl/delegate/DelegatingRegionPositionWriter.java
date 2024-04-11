package me.supcheg.advancedmanhunt.bridge.impl.delegate;

import me.supcheg.advancedmanhunt.bridge.RegionPositionWriter;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsRegionPositionWriter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.nio.file.Path;

public class DelegatingRegionPositionWriter extends DelegatingBridge<RegionPositionWriter>
        implements RegionPositionWriter {
    @Inject
    public DelegatingRegionPositionWriter() {
        super(
                NmsRegionPositionWriter::new
        );
    }

    @Override
    public void writePositionsToRegion(@NotNull Path regionPath) {
        delegate.writePositionsToRegion(regionPath);
    }
}
