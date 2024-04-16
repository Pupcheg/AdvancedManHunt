package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.supcheg.advancedmanhunt.bridge.command.EnumArgument;
import me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class RealEnvironmentArgument {
    private final EnumArgument enumArgument;
    private final SuggestionProvider<BukkitBrigadierCommandSource> environmentsProvider;

    @Inject
    public RealEnvironmentArgument(@NotNull EnumArgument enumArgument) {
        this.enumArgument = enumArgument;
        this.environmentsProvider = BukkitBrigadierCommands.suggestEnumConstants(RealEnvironment.class);
    }

    @NotNull
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> environment(@NotNull String name) {
        return enumArgument.enumArg(name, RealEnvironment.class).suggests(environmentsProvider);
    }

    @NotNull
    public RealEnvironment getEnvironment(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        return enumArgument.getEnum(ctx, name, RealEnvironment.class);
    }
}
