package me.supcheg.advancedmanhunt.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface TemplateRepository {
    @NotNull
    @Unmodifiable
    List<Template> getTemplates();

    @Nullable
    Template getTemplate(@NotNull String name);

    void addTemplate(@NotNull Template template);

    @Nullable
    Template removeTemplate(@NotNull String name);

    void removeTemplate(@NotNull Template template);

    void loadTemplates();
}
