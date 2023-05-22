package me.supcheg.advancedmanhunt.command;

import co.aikar.commands.PaperCommandManager;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.resolver.DefaultResolver;
import me.supcheg.advancedmanhunt.command.resolver.DistanceResolver;
import me.supcheg.advancedmanhunt.command.resolver.EnvironmentResolver;
import me.supcheg.advancedmanhunt.command.resolver.KeyedCoordResolver;
import me.supcheg.advancedmanhunt.command.resolver.PathResolver;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class AdvancedManHuntCommandManager extends PaperCommandManager {
    private final AdvancedManHuntPlugin plugin;

    public AdvancedManHuntCommandManager(@NotNull AdvancedManHuntPlugin plugin) {
        super(plugin.getBukkitPlugin());
        this.plugin = plugin;
    }

    public void setup() {
        enableUnstableAPI("brigadier");

        registerResolver(Distance.class, new DistanceResolver());
        registerResolver(Path.class, new PathResolver());
        registerResolver(KeyedCoord.class, new KeyedCoordResolver());
        registerResolver(World.Environment.class, new EnvironmentResolver());

        registerCommand(new GameCommand(plugin));
        registerCommand(new RegionCommand(plugin));
        registerCommand(new TemplateCommand(plugin));
    }

    public <T> void registerResolver(@NotNull Class<T> type, @NotNull DefaultResolver<T> resolver) {
        getCommandContexts().registerContext(type, resolver);

        String id = '@' + type.getSimpleName().toLowerCase();
        getCommandCompletions().registerCompletion(id, resolver);
        getCommandCompletions().setDefaultCompletion(id, type);
    }
}
