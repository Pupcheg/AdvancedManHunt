package me.supcheg.advancedmanhunt.bridge.impl.delegate;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.supcheg.advancedmanhunt.bridge.BrigadierCommandRegisterer;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsBrigadierCommandRegisterer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class DelegatingBrigadierCommandRegisterer extends DelegatingBridge<BrigadierCommandRegisterer>
        implements BrigadierCommandRegisterer {
    @Inject
    public DelegatingBrigadierCommandRegisterer() {
        super(
                NmsBrigadierCommandRegisterer::new
        );
    }

    @Override
    public void registerCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> command) {
        delegate.registerCommand(command);
    }
}
