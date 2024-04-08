package me.supcheg.advancedmanhunt.bridge.impl.nms;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.supcheg.advancedmanhunt.bridge.BrigadierCommandRegisterer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class NmsBrigadierCommandRegisterer implements BrigadierCommandRegisterer {
    @Override
    public void registerCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> command) {
        MinecraftServer.getServer().getCommands().getDispatcher().register(uncheckedCast(command));
    }
}
