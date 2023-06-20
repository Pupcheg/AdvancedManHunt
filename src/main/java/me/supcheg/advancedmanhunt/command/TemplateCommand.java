package me.supcheg.advancedmanhunt.command;

import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.task.TemplateCreateConfig;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TemplateCommand extends Command {
    private final AdvancedManHuntPlugin plugin;

    public TemplateCommand(@NotNull AdvancedManHuntPlugin plugin) {
        super("template");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        plugin.getTemplateTaskFactory().runCreateTask(
                sender,
                TemplateCreateConfig.builder()
                        .worldName("standard_template_1")
                        .sideSize(Distance.ofRegions(2))
                        .environment(World.Environment.NORMAL)
                        .out(plugin.getContainerAdapter().resolveData("standard_template_1"))
                        .build()
        );

        return true;
    }
}
