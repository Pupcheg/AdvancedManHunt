package me.supcheg.advancedmanhunt.test.structure;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.GameCommand;
import me.supcheg.advancedmanhunt.command.TemplateCommand;
import org.jetbrains.annotations.NotNull;

public class TestCommandDispatcher extends CommandDispatcher<BukkitBrigadierCommandSource> {

    public TestCommandDispatcher(@NotNull AdvancedManHuntPlugin plugin) {
        new GameCommand(plugin).register(this);
        new TemplateCommand(plugin).register(this);
    }
}
