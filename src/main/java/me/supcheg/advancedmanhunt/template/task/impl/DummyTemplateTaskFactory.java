package me.supcheg.advancedmanhunt.template.task.impl;

import me.supcheg.advancedmanhunt.template.task.TemplateCreateConfig;
import me.supcheg.advancedmanhunt.template.task.TemplateTaskFactory;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DummyTemplateTaskFactory implements TemplateTaskFactory {
    @Override
    public void runCreateTask(@NotNull CommandSender sender, @NotNull TemplateCreateConfig config) {
        sender.sendPlainMessage("'Chunky' plugin not found!");
    }
}
