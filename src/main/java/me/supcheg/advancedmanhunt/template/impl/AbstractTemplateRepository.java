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

    public AbstractTemplateRepository() {
        this.name2template = new HashMap<>();
        this.unmodifiableTemplates = Collections.unmodifiableCollection(name2template.values());
    }

    @NotNull
    @UnmodifiableView
    public Collection<Template> getTemplates() {
        return unmodifiableTemplates;
    }

    @Nullable
    public Template getTemplate(@NotNull String name) {
        return name2template.get(name);
    }

    public void addTemplate(@NotNull Template template) {
        name2template.put(template.getName(), template);
    }

    @Nullable
    public Template removeTemplate(@NotNull String name) {
        return name2template.remove(name);
    }

    public boolean removeTemplate(@NotNull Template template) {
        return name2template.remove(template.getName(), template);
    }
}
