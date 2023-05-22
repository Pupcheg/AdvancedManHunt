package me.supcheg.advancedmanhunt.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.player.Permissions;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.task.TemplateCreateConfig;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@CommandAlias("template")
@CommandPermission(Permissions.TEMPLATE_COMMAND)
@AllArgsConstructor
public class TemplateCommand extends BaseCommand {

    private final AdvancedManHuntPlugin plugin;

    @Subcommand("create")
    public void create(@NotNull CommandSender commandSender,
                       @Name("name") String name, @Name("side_size") Distance sideSize,
                       @Name("environment") World.Environment environment, @Name("out_path") Path out) {
        plugin.getTemplateTaskFactory().runCreateTask(
                commandSender,
                TemplateCreateConfig.builder()
                        .worldName("amh_generate_" + name)
                        .sideSize(sideSize)
                        .environment(environment)
                        .out(plugin.resolveDataPath(out))
                        .build()
        );

    }

    @Subcommand("load")
    public void load(@NotNull CommandSender commandSender) {
        plugin.getTemplateRepository().loadTemplates();
        commandSender.sendPlainMessage("Loaded");
    }

    @Subcommand("add")
    public void add(@NotNull CommandSender commandSender,
                    @Name("name") String name, @Name("side_size") Distance sideSize, @Name("path") Path path) {
        Template template = new Template(name, sideSize, path);
        plugin.getTemplateRepository().addTemplate(template);

        commandSender.sendPlainMessage("Added: " + template);
    }

    @Subcommand("remove")
    public void remove(@NotNull CommandSender commandSender, @Name("name") String name) {
        Template template = plugin.getTemplateRepository().removeTemplate(name);

        commandSender.sendPlainMessage("Removed: " + template);
    }

    @Subcommand("list")
    public void list(@NotNull CommandSender commandSender) {
        for (Template template : plugin.getTemplateRepository().getTemplates()) {
            commandSender.sendPlainMessage(template.toString());
        }
    }
}
