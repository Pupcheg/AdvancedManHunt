package me.supcheg.advancedmanhunt.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import org.jetbrains.annotations.NotNull;

public final class RealEnvironmentArgumentType extends EnumArgumentType<RealEnvironment> {

    private RealEnvironmentArgumentType() {
        super(RealEnvironment.class);
    }

    @NotNull
    public static ArgumentType<RealEnvironment> environment() {
        return new RealEnvironmentArgumentType();
    }

    @NotNull
    public static RealEnvironment getEnvironment(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull String name) {
        return ctx.getArgument(name, RealEnvironment.class);
    }
}
