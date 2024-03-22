package me.supcheg.advancedmanhunt.template.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.CustomLog;
import lombok.Getter;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.storage.InMemoryEntityRepository;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.json.DistanceSerializer;
import me.supcheg.advancedmanhunt.template.json.ImmutableLocationSerializer;
import me.supcheg.advancedmanhunt.template.json.SerializedTemplate;
import me.supcheg.advancedmanhunt.template.json.SpawnLocationFindResultSerializer;
import me.supcheg.advancedmanhunt.template.json.TemplateSerializer;
import me.supcheg.advancedmanhunt.util.MapTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@CustomLog
@Getter
public class DefaultTemplateRepository extends InMemoryEntityRepository<Template, String> implements TemplateRepository {
    private final Gson gson;

    public DefaultTemplateRepository(@NotNull ContainerAdapter containerAdapter) {
        super(Template::getName);
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(
                        new MapTypeAdapterFactory()
                                .typeAdapter(SerializedTemplate.class, TemplateSerializer::new)
                                .typeAdapter(ImmutableLocation.class, ImmutableLocationSerializer::new)
                                .typeAdapter(Distance.class, DistanceSerializer::new)
                                .typeAdapter(SpawnLocationFindResult.class, SpawnLocationFindResultSerializer::new)
                )
                .create();

        loadDirectory(containerAdapter.resolveData("templates"));
    }

    @SneakyThrows
    public void loadDirectory(@NotNull Path root) {
        Files.createDirectories(root);
        try (Stream<Path> walk = Files.walk(root)) {
            walk.filter(path -> path.getFileName().toString().equalsIgnoreCase("template.json"))
                    .forEach(templateJson -> {
                        try {
                            loadTemplateFile(templateJson);
                        } catch (Throwable thr) {
                            log.error("An error occurred while loading '{}'", templateJson, thr);
                        }
                    });
        }
    }

    @SneakyThrows
    public void loadTemplateFile(@NotNull Path templateJson) {
        SerializedTemplate serializedTemplate;
        try (BufferedReader in = Files.newBufferedReader(templateJson)) {
            serializedTemplate = gson.fromJson(in, SerializedTemplate.class);
        }

        Template template = serializedTemplate.toTemplate(templateJson.getParent());
        storeEntity(template);
    }

    @Override
    public void save() {
        getEntities().forEach(this::saveTemplate);
    }

    @SneakyThrows
    public void saveTemplate(@NotNull Template template) {
        Path templateJson = template.getFolder().resolve("template.json");
        SerializedTemplate serializedTemplate = SerializedTemplate.fromTemplate(template);

        try (BufferedWriter out = Files.newBufferedWriter(templateJson)) {
            gson.toJson(serializedTemplate, SerializedTemplate.class, out);
        }
    }
}
