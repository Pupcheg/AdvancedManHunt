package me.supcheg.advancedmanhunt.paper;

import com.google.gson.Gson;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import me.supcheg.advancedmanhunt.json.Types;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
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
public class PaperPluginLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        PluginProviderContext context = classpathBuilder.getContext();

        Collection<String> repositories = Collections.emptySet();
        Collection<String> dependencies = Collections.emptySet();

        try (FileSystem fileSystem = FileSystems.newFileSystem(context.getPluginSource())) {
            Path dependenciesPath = fileSystem.getPath("dependencies.json");

            if (Files.exists(dependenciesPath)) {

                try (Reader reader = Files.newBufferedReader(dependenciesPath)) {
                    Type type = Types.type(Map.class, String.class, Types.type(List.class, String.class));

                    Map<String, List<String>> data = new Gson().fromJson(reader, type);

                    repositories = Objects.requireNonNullElse(data.get("repositories"), Collections.emptySet());
                    dependencies = Objects.requireNonNullElse(data.get("dependencies"), Collections.emptySet());
                }
            }
        } catch (Exception ex) {
            context.getLogger().error("An error occurred while loading dependencies", ex);
            return;
        }

        MavenLibraryResolver mavenLibraryResolver = new MavenLibraryResolver();

        repositories.stream()
                .map(url -> new RemoteRepository.Builder(null, "default", url))
                .map(RemoteRepository.Builder::build)
                .forEach(mavenLibraryResolver::addRepository);

        dependencies.stream()
                .map(DefaultArtifact::new)
                .map(artifact -> new Dependency(artifact, "runtime"))
                .forEach(mavenLibraryResolver::addDependency);

        classpathBuilder.addLibrary(mavenLibraryResolver);
    }
}
