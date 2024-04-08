package me.supcheg.advancedmanhunt.bridge.impl.lazy;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.supcheg.advancedmanhunt.bridge.KeyArgument;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsKeyArgument;
import me.supcheg.advancedmanhunt.bridge.impl.safe.StringWrappingKeyArgument;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class LazyKeyArgument extends LazyBridge<KeyArgument>
        implements KeyArgument {
    @Inject
    public LazyKeyArgument() {
        super(KeyArgument.class,
                NmsKeyArgument::new,
                StringWrappingKeyArgument::new
        );
    }

    @NotNull
    @Override
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> key(@NotNull String name) {
        return delegate().key(name);
    }

    @NotNull
    @Override
    public Key getKey(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name) {
        return delegate().getKey(ctx, name);
    }
}
