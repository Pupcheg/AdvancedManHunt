package me.supcheg.advancedmanhunt.bridge;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.NotNull;

public interface BrigadierCommandRegisterer {
    void registerCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> command);
}
