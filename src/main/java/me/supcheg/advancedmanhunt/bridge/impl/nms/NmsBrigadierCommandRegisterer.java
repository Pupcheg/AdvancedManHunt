package me.supcheg.advancedmanhunt.bridge.impl.nms;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.supcheg.advancedmanhunt.bridge.BrigadierCommandRegisterer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class NmsBrigadierCommandRegisterer implements BrigadierCommandRegisterer {
    private final CommandDispatcher<CommandSourceStack> dispatcher =
            MinecraftServer.getServer().getCommands().getDispatcher();

    @Override
    public void registerCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> command) {
        dispatcher.register(uncheckedCast(command));
    }
}
