package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.supcheg.advancedmanhunt.bridge.command.EnumArgument;
import me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class ManHuntRoleArgument {
    private final EnumArgument enumArgument;
    private final SuggestionProvider<BukkitBrigadierCommandSource> rolesProvider;

    @Inject
    public ManHuntRoleArgument(@NotNull EnumArgument enumArgument) {
        this.enumArgument = enumArgument;
        this.rolesProvider = BukkitBrigadierCommands.suggestEnumConstants(RealEnvironment.class);
    }

    @NotNull
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> role(@NotNull String name) {
        return enumArgument.enumArg(name, ManHuntRole.class).suggests(rolesProvider);
    }

    @NotNull
    public ManHuntRole getRole(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        return enumArgument.getEnum(ctx, name, ManHuntRole.class);
    }
}
