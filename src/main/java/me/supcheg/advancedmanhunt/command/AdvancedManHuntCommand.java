package me.supcheg.advancedmanhunt.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class AdvancedManHuntCommand implements BrigadierCommand {
    private final GameCommand game;
    private final TemplateCommand template;
    private final DebugCommand debug;

    @NotNull
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> command = literal(AdvancedManHuntPlugin.NAMESPACE);
        game.appendTo(command);
        template.appendTo(command);
        debug.appendTo(command);
        return command;
    }

    @NotNull
    @Override
    public List<String> aliases() {
        return List.of("amh");
    }
}
