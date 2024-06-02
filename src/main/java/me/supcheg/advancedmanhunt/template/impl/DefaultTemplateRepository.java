package me.supcheg.advancedmanhunt.template.impl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.math.distance.Distance;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.json.adapter.DistanceAdapter;
import me.supcheg.advancedmanhunt.json.adapter.ImmutableLocationAdapter;
import me.supcheg.advancedmanhunt.json.adapter.KeyAdapter;
import me.supcheg.advancedmanhunt.storage.InMemoryEntityRepository;
import me.supcheg.advancedmanhunt.template.SerializedTemplate;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.json.MapTypeAdapterFactory;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
@Getter
public class DefaultTemplateRepository extends InMemoryEntityRepository<Template, Key> implements TemplateRepository {
    private final Gson gson;

    @Inject
    public DefaultTemplateRepository(@NotNull ContainerAdapter containerAdapter) {
        super(Template::getKey);
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(
                        new MapTypeAdapterFactory(3)
                                .typeAdapter(ImmutableLocation.class, ImmutableLocationAdapter::new)
                                .typeAdapter(Distance.class, DistanceAdapter::new)
                                .typeAdapter(Key.class, KeyAdapter::new)
                )
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        loadMainDirectory(containerAdapter);
    }

    public void loadMainDirectory(@NotNull ContainerAdapter containerAdapter) {
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
        super.save();
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
