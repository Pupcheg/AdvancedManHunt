package me.supcheg.advancedmanhunt.bridge.impl.delegate;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.supcheg.advancedmanhunt.bridge.command.EnumArgument;
import me.supcheg.advancedmanhunt.bridge.impl.safe.StringWrappingEnumArgument;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class DelegatingEnumArgument extends DelegatingBridge<EnumArgument> implements EnumArgument {
    @Inject
    public DelegatingEnumArgument() {
        super(
                StringWrappingEnumArgument::new
        );
    }

    @NotNull
    @Override
    public <E extends Enum<E>> RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> enumArg(@NotNull String name,
                                                                                                @NotNull Class<E> enumType) {
        return delegate.enumArg(name, enumType);
    }

    @NotNull
    @Override
    public <E extends Enum<E>> E getEnum(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name,
                                         @NotNull Class<E> enumType) throws CommandSyntaxException {
        return delegate.getEnum(ctx, name, enumType);
    }
}
