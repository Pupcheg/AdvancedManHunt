package me.supcheg.advancedmanhunt.template.task.impl;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.Message;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.task.TemplateCreateConfig;
import me.supcheg.advancedmanhunt.template.task.TemplateTaskFactory;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.popcraft.chunky.Chunky;
import org.popcraft.chunky.ChunkyBukkit;
import org.popcraft.chunky.GenerationTask;
import org.popcraft.chunky.Selection;
import org.popcraft.chunky.platform.BukkitWorld;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChunkyTemplateTaskFactory implements TemplateTaskFactory {
    private final AdvancedManHuntPlugin plugin;
    private final CustomLogger logger;
    private final Chunky chunky;
    private final Executor syncExecutor;

    public ChunkyTemplateTaskFactory(@NotNull AdvancedManHuntPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getSLF4JLogger().newChild(ChunkyTemplateTaskFactory.class);

        var chunkyBukkit = (ChunkyBukkit) Bukkit.getPluginManager().getPlugin("Chunky");
        Objects.requireNonNull(chunkyBukkit, "'Chunky' not found!");

        this.chunky = chunkyBukkit.getChunky();

        var bukkitPlugin = plugin.getBukkitPlugin();
        this.syncExecutor = task -> Bukkit.getScheduler().runTask(bukkitPlugin, task);
    }

    @Override
    public void runCreateTask(@NotNull CommandSender sender, @NotNull TemplateCreateConfig config) {
        World bukkitWorld = WorldCreator.name(config.getWorldName())
                .environment(config.getEnvironment())
                .seed(config.getSeed())
                .createWorld();

        if (bukkitWorld == null) {
            sender.sendPlainMessage("Can't create world with config: " + config);
            return;
        }

        int radiusInBlocks = config.getSideSize().getBlocks();

        Selection selection = Selection.builder(chunky, new BukkitWorld(bukkitWorld))
                .center(0, 0)
                .radiusX(radiusInBlocks)
                .radiusZ(radiusInBlocks)
                .build();

        GenerationTask generationTask = new GenerationTask(chunky, selection);
        chunky.getGenerationTasks().put(config.getWorldName(), generationTask);

        chunky.getScheduler().runTask(() -> {
            generationTask.run();
            afterWorldGeneration(config);
        });
    }

    @SneakyThrows
    private void afterWorldGeneration(@NotNull TemplateCreateConfig config) {
        String worldName = config.getWorldName();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Message.NO_WORLD.broadcast(worldName);
            return;
        }
        Path worldFolder = WorldReference.of(world).getFolder();

        CompletableFuture.runAsync(() -> Bukkit.unloadWorld(worldName, true), syncExecutor).join();

        if (Bukkit.getWorld(worldName) != null) {
            Message.CANNOT_UNLOAD.broadcast(worldName);
            return;
        }

        Path outPath = config.getOut();

        try {
            Files.createDirectories(outPath);

            List<String> subPaths = List.of("entities", "poi", "region");

            for (String subPath : subPaths) {
                Files.move(worldFolder.resolve(subPath), outPath.resolve(subPath), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            Message.CANNOT_MOVE_DATA.broadcast(worldName, outPath);
            logger.error("An error occurred while moving world files", e);
            return;
        }

        Template template = new Template(
                outPath.getFileName().toString(),
                config.getSideSize(),
                outPath
        );

        plugin.getTemplateRepository().addTemplate(template);

        Message.SUCCESSFUL_TEMPLATE_CREATE.broadcast(template.getName(), template.getSideSize(), template.getFolder());
    }
}
