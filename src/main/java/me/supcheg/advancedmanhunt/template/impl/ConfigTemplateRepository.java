package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.template.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigTemplateRepository extends AbstractTemplateRepository {
    private static final Type REGION_TEMPLATE_LIST_TYPE = Types.type(List.class, Template.class);

    private final AdvancedManHuntPlugin plugin;
    private final CustomLogger logger;
    private final Path templatesPath;

    public ConfigTemplateRepository(@NotNull AdvancedManHuntPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getSLF4JLogger().newChild(ConfigTemplateRepository.class);
        this.templatesPath = plugin.getContainerAdapter().resolveData("templates.json");
        loadTemplates();
    }

    @Override
    public void addTemplate(@NotNull Template template) {
        super.addTemplate(template);
        updateFile();
    }

    @Nullable
    @Override
    public Template removeTemplate(@NotNull String name) {
        var removed = super.removeTemplate(name);
        if (removed != null) {
            updateFile();
        }
        return removed;
    }

    @Override
    public boolean removeTemplate(@NotNull Template template) {
        if (super.removeTemplate(template)) {
            updateFile();
            return true;
        }
        return false;
    }

    public void loadTemplates() {
        try {
            if (Files.exists(templatesPath)) {
                try (Reader reader = Files.newBufferedReader(templatesPath)) {
                    List<Template> templates = plugin.getGson().fromJson(reader, REGION_TEMPLATE_LIST_TYPE);
                    for (Template template : templates) {
                        name2template.put(template.getName(), template);
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("An error occurred while loading templates from {}", templatesPath, ex);
        }
    }

    protected void updateFile() {
        String json = plugin.getGson().toJson(name2template.values());
        try {
            Files.writeString(templatesPath, json);
        } catch (IOException ex) {
            logger.error("An error occurred while saving template to {}", templatesPath, ex);
        }
    }
}
