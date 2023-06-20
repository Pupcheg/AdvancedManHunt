package me.supcheg.advancedmanhunt.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface TemplateRepository {
    @NotNull
    @UnmodifiableView
    Collection<Template> getTemplates();

    @Nullable
    Template getTemplate(@NotNull String name);

    void addTemplate(@NotNull Template template);

    @Nullable
    Template removeTemplate(@NotNull String name);

    boolean removeTemplate(@NotNull Template template);
}
