package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTemplateRepository implements TemplateRepository {

    protected final Map<String, Template> name2template;
    protected final Collection<Template> unmodifiableTemplates;
    protected final Map<String, Template> unmodifiableName2template;

    public AbstractTemplateRepository() {
        this.name2template = new HashMap<>();
        this.unmodifiableTemplates = Collections.unmodifiableCollection(name2template.values());
        this.unmodifiableName2template = Collections.unmodifiableMap(name2template);
    }

    @Override
    @NotNull
    @UnmodifiableView
    public Collection<Template> getTemplates() {
        return unmodifiableTemplates;
    }

    @Override
    @NotNull
    @UnmodifiableView
    public Map<String, Template> getTemplatesMap() {
        return unmodifiableName2template;
    }

    @Override
    @Nullable
    public Template getTemplate(@NotNull String name) {
        return name2template.get(name);
    }

    @Override
    public void addTemplate(@NotNull Template template) {
        name2template.put(template.getName(), template);
    }

    @Override
    @Nullable
    public Template removeTemplate(@NotNull String name) {
        return name2template.remove(name);
    }

    @Override
    public boolean removeTemplate(@NotNull Template template) {
        return name2template.remove(template.getName(), template);
    }
}
