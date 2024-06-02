package me.supcheg.advancedmanhunt.command.argument;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import org.jetbrains.annotations.NotNull;

public final class ManHuntRoleArgumentType extends EnumArgumentType<ManHuntRole> {
    private ManHuntRoleArgumentType() {
        super(ManHuntRole.class);
    }

    @NotNull
    public static EnumArgumentType<ManHuntRole> manhuntRole() {
        return new ManHuntRoleArgumentType();
    }

    @NotNull
    public static ManHuntRole getManhuntRole(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull String name) {
        return ctx.getArgument(name, ManHuntRole.class);
    }
}
