package me.supcheg.advancedmanhunt.bridge.impl.delegate;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.supcheg.advancedmanhunt.bridge.command.UniqueIdArgument;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsUniqueIdArgument;
import me.supcheg.advancedmanhunt.bridge.impl.safe.StringWrappingUniqueIdArgument;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;

public class DelegatingUniqueIdArgument extends DelegatingBridge<UniqueIdArgument> implements UniqueIdArgument {
    @Inject
    protected DelegatingUniqueIdArgument() {
        super(
                NmsUniqueIdArgument::new,
                StringWrappingUniqueIdArgument::new
        );
    }

    @NotNull
    @Override
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> uniqueId(@NotNull String name) {
        return delegate.uniqueId(name);
    }

    @NotNull
    @Override
    public UUID getUniqueId(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name) {
        return delegate.getUniqueId(ctx, name);
    }
}
