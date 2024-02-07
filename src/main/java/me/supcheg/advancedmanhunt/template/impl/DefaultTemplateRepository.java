package me.supcheg.advancedmanhunt.template.impl;

import com.google.gson.GsonBuilder;
import me.supcheg.advancedmanhunt.storage.PathSerializingEntityRepository;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.json.TemplateJsonSerializer;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.jetbrains.annotations.NotNull;

public class DefaultTemplateRepository extends PathSerializingEntityRepository<Template, String> implements TemplateRepository {
    public DefaultTemplateRepository(@NotNull ContainerAdapter containerAdapter) {
        super(
                containerAdapter.resolveData("templates.json"),
                new GsonBuilder()
                        .registerTypeAdapterFactory(new TemplateJsonSerializer())
                        .create(),
                Template.class,
                Template::getName
        );
    }
}
