package me.supcheg.advancedmanhunt.paper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import lombok.SneakyThrows;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@SuppressWarnings("UnstableApiUsage")
public class ConfigLibraryResolver implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        PluginProviderContext ctx = classpathBuilder.getContext();

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
            Path dependenciesPath = fs.getPath("paper-libraries.json");

            if (Files.notExists(dependenciesPath)) {
                return;
            }

            JsonObject data;
            try (Reader reader = Files.newBufferedReader(dependenciesPath)) {
                data = JsonParser.parseReader(reader).getAsJsonObject();
            }

            repositories = data.getAsJsonObject("repositories").asMap().values()
                    .stream()
                    .map(JsonElement::getAsString)
                    .toList();
            dependencies = data.getAsJsonArray("dependencies").asList().stream()
                    .map(JsonElement::getAsString)
                    .toList();
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
