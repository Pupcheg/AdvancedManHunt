package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.controller.DefaultAdvancedGuiController;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class GuiTestCommand extends AbstractCommand {

    private final AdvancedGui gui;

    public GuiTestCommand(Plugin plugin, EventListenerRegistry registry) {
        DefaultAdvancedGuiController controller = new DefaultAdvancedGuiController(plugin, registry);

        this.gui = controller.gui()
                .button(controller.button()
                        .slot(10)
                        .clickAction("def", ctx -> ctx.getButton().setEnchanted(!ctx.getButton().isEnchanted()))
                )
                .buildAndRegister();
    }

    @Override
    public void register(@NotNull CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher) {
        commandDispatcher.register(literal("gui")
                .executes(context -> {
                    gui.open((Player) context.getSource().getBukkitSender());
                    return 1;
                })
        );
    }
}
