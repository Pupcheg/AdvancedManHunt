package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigTemplateRepository implements TemplateRepository {
    private static final Type REGION_TEMPLATE_LIST_TYPE = Types.type(List.class, Template.class);

    private final AdvancedManHuntPlugin plugin;
    private final Path templatesPath;

    private final Map<String, Template> name2templates;
    private final List<Template> templates;
    private final List<Template> unmodifiableTemplates;

    public ConfigTemplateRepository(@NotNull AdvancedManHuntPlugin plugin) {
        this.plugin = plugin;
        this.templatesPath = plugin.resolveDataPath("templates.json");
        this.name2templates = new HashMap<>();
        this.templates = new ArrayList<>();
        this.unmodifiableTemplates = Collections.unmodifiableList(templates);
        loadTemplates();
    }

    @NotNull
    @Unmodifiable
    @Override
    public List<Template> getTemplates() {
        return unmodifiableTemplates;
    }

    @Nullable
    @Override
    public Template getTemplate(@NotNull String name) {
        return name2templates.get(name);
    }

    @Override
    public void addTemplate(@NotNull Template template) {
        templates.add(template);
        name2templates.put(template.getName(), template);
        updateFile();
    }

    @Nullable
    @Override
    public Template removeTemplate(@NotNull String name) {
        Template template = name2templates.remove(name);
        if (template != null) {
            templates.remove(template);
        }
        return template;
    }

    @Override
    public void removeTemplate(@NotNull Template template) {
        if (templates.remove(template)) {
            name2templates.remove(template.getName());
            updateFile();
        }
    }

    @Override
    public void loadTemplates() {
        try {
            if (Files.exists(templatesPath)) {
                try (Reader reader = Files.newBufferedReader(templatesPath)) {
                    List<Template> templates = plugin.getGson().fromJson(reader, REGION_TEMPLATE_LIST_TYPE);
                    this.templates.addAll(templates);
                    for (Template template : templates) {
                        name2templates.put(template.getName(), template);
                    }
                }
            }
        } catch (IOException ex) {
            plugin.getSLF4JLogger().error("", ex);
        }
    }

    private void updateFile() {
        String json = plugin.getGson().toJson(templates);
        try {
            Files.writeString(templatesPath, json);
        } catch (IOException ex) {
            plugin.getSLF4JLogger().error("", ex);
        }
    }
}
