package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.literal;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AdvancedManHuntCommand implements BukkitBrigadierCommand {
    private final GameCommand game;
    private final TemplateCommand template;
    private final DebugCommand debug;

    @NotNull
    @Override
    public LiteralArgumentBuilder<BukkitBrigadierCommandSource> build() {
        LiteralArgumentBuilder<BukkitBrigadierCommandSource> command = literal(AdvancedManHuntPlugin.NAMESPACE);
        game.appendTo(command);
        template.appendTo(command);
        debug.appendTo(command);
        return command;
    }
}
