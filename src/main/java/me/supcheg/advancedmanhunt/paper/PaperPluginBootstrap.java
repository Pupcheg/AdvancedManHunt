package me.supcheg.advancedmanhunt.paper;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import me.supcheg.advancedmanhunt.region.impl.DefaultContainerAdapter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class PaperPluginBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull PluginProviderContext context) {
    }

    @NotNull
    @Override
    public JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new PaperPlugin(new DefaultContainerAdapter());
    }
}
