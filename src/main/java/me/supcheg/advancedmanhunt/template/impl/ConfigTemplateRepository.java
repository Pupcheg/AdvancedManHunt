package me.supcheg.advancedmanhunt.template.impl;

import com.google.gson.Gson;
import lombok.CustomLog;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@CustomLog
public class ConfigTemplateRepository extends AbstractTemplateRepository {
    private static final Type REGION_TEMPLATE_LIST_TYPE = Types.type(List.class, Template.class);

    private final Gson gson;
    private final Path templatesPath;

    public ConfigTemplateRepository(@NotNull Gson gson, @NotNull ContainerAdapter containerAdapter) {
        this.gson = gson;
        this.templatesPath = containerAdapter.resolveData("templates.json");
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
        Template removed = super.removeTemplate(name);
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
        if (Files.notExists(templatesPath)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(templatesPath)) {
            List<Template> templates = gson.fromJson(reader, REGION_TEMPLATE_LIST_TYPE);
            for (Template template : templates) {
                name2template.put(template.getName(), template);
            }
        } catch (Exception ex) {
            log.error("An error occurred while loading templates from {}", templatesPath, ex);
        }
    }

    protected void updateFile() {
        try (Writer writer = Files.newBufferedWriter(templatesPath)) {
            gson.toJson(name2template.values(), REGION_TEMPLATE_LIST_TYPE, writer);
        } catch (Exception ex) {
            log.error("An error occurred while saving templates to {}", templatesPath, ex);
        }
    }
}
