package me.supcheg.advancedmanhunt.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.supcheg.advancedmanhunt.injector.Injector;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class InjectorBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext ctx) {
        Injector.initialize();
    }
}
