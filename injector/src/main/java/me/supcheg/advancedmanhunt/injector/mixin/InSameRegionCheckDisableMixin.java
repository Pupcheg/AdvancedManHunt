package me.supcheg.advancedmanhunt.injector.mixin;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RegionFile.class)
public class InSameRegionCheckDisableMixin {
    /**
     * @author Supcheg
     * @reason Disable same region check
     */
    @Overwrite
    private static boolean inSameRegionfile(ChunkPos first, ChunkPos second) {
        return true;
    }
}
