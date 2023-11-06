package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.text.MessageText;
import me.supcheg.advancedmanhunt.template.TemplateCreateConfig;
import me.supcheg.advancedmanhunt.template.TemplateTaskFactory;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DummyTemplateTaskFactory implements TemplateTaskFactory {
    @Override
    public void runCreateTask(@NotNull CommandSender sender, @NotNull TemplateCreateConfig config) {
        MessageText.PLUGIN_NOT_FOUND.send(sender, "Chunky");
    }
}
