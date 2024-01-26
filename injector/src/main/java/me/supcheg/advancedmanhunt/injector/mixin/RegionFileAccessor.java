package me.supcheg.advancedmanhunt.injector.mixin;

import net.minecraft.world.level.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RegionFile.class)
public interface RegionFileAccessor {
    @Invoker
    boolean invokeIsOversized(int x, int z);
}
