package me.supcheg.advancedmanhunt.paper;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.json.Types;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class ConfigLibraryResolver implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        var ctx = classpathBuilder.getContext();

        MavenLibraryResolver libraryResolver = new MavenLibraryResolver();
        resolveAll(libraryResolver, ctx.getPluginSource());

        classpathBuilder.addLibrary(libraryResolver);
    }

    @SneakyThrows
    @Contract(mutates = "param1")
    public static void resolveAll(@NotNull MavenLibraryResolver libraryResolver, @NotNull Path pluginSource) {
        Collection<String> repositories;
        Collection<String> dependencies;

        try (FileSystem fs = FileSystems.newFileSystem(pluginSource)) {
            Path dependenciesPath = fs.getPath("dependencies.json");

            if (Files.notExists(dependenciesPath)) {
                return;
            }

            Map<String, List<String>> data;
            try (Reader reader = Files.newBufferedReader(dependenciesPath)) {
                Type type = Types.type(Map.class, String.class, Types.type(List.class, String.class));
                data = new Gson().fromJson(reader, type);
            }

            repositories = Objects.requireNonNullElse(data.get("repositories"), Collections.emptySet());
            dependencies = Objects.requireNonNullElse(data.get("dependencies"), Collections.emptySet());
        }

        repositories.stream()
                .map(url -> new Builder(null, "default", url))
                .map(Builder::build)
                .forEach(libraryResolver::addRepository);

        dependencies.stream()
                .map(DefaultArtifact::new)
                .map(artifact -> new Dependency(artifact, "runtime"))
                .forEach(libraryResolver::addDependency);
    }
}
