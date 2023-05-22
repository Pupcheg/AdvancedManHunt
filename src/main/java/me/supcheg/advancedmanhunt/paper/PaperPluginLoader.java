package me.supcheg.advancedmanhunt.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class PaperPluginLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver mavenLibraryResolver = new MavenLibraryResolver();

        mavenLibraryResolver.addRepository(
                new RemoteRepository.Builder("aikar", "default", "https://repo.aikar.co/content/groups/aikar").build()
        );
        
        mavenLibraryResolver.addDependency(
                new Dependency(new DefaultArtifact("co.aikar:acf-paper:0.5.1-SNAPSHOT"), "runtime")
        );

        classpathBuilder.addLibrary(mavenLibraryResolver);
    }
}
