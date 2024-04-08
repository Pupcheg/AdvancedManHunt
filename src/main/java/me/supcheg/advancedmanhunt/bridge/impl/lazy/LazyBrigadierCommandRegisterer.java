package me.supcheg.advancedmanhunt.bridge.impl.lazy;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.supcheg.advancedmanhunt.bridge.BrigadierCommandRegisterer;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsBrigadierCommandRegisterer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class LazyBrigadierCommandRegisterer extends LazyBridge<BrigadierCommandRegisterer>
        implements BrigadierCommandRegisterer {
    @Inject
    public LazyBrigadierCommandRegisterer() {
        super(BrigadierCommandRegisterer.class,
                NmsBrigadierCommandRegisterer::new
        );
    }

    @Override
    public void registerCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> command) {
        delegate().registerCommand(command);
    }
}
