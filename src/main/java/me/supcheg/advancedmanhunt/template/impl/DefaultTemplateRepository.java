package me.supcheg.advancedmanhunt.template.impl;

import com.google.gson.GsonBuilder;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.storage.PathSerializingEntityRepository;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.json.DistanceSerializer;
import me.supcheg.advancedmanhunt.template.json.ImmutableLocationSerializer;
import me.supcheg.advancedmanhunt.template.json.SpawnLocationFindResultSerializer;
import me.supcheg.advancedmanhunt.template.json.TemplateSerializer;
import me.supcheg.advancedmanhunt.util.MapTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;

public class DefaultTemplateRepository extends PathSerializingEntityRepository<Template, String> implements TemplateRepository {
    public DefaultTemplateRepository(@NotNull ContainerAdapter containerAdapter) {
        super(
                containerAdapter.resolveData("templates.json"),
                new GsonBuilder()
                        .registerTypeAdapterFactory(
                                new MapTypeAdapterFactory()
                                        .typeAdapter(Template.class, TemplateSerializer::new)
                                        .typeAdapter(ImmutableLocation.class, ImmutableLocationSerializer::new)
                                        .typeAdapter(Distance.class, DistanceSerializer::new)
                                        .typeAdapter(SpawnLocationFindResult.class, SpawnLocationFindResultSerializer::new)
                        )
                        .create(),
                Template.class,
                Template::getName
        );
    }
}
