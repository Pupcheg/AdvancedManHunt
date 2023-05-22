package me.supcheg.advancedmanhunt.template.task;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface TemplateTaskFactory {
    void runCreateTask(@NotNull CommandSender sender, @NotNull TemplateCreateConfig config);
}
