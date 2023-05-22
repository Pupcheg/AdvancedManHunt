package me.supcheg.advancedmanhunt.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.region.GameRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Region.MAX_REGIONS_PER_WORLD;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Region.MAX_WORLDS_PER_ENVIRONMENT;

@CommandAlias("region")
@AllArgsConstructor
public class RegionCommand extends BaseCommand {

    private final AdvancedManHuntPlugin plugin;

    @Subcommand("list")
    public void list(@NotNull CommandSender commandSender) {

        Map<World, Integer> world2regionsCount = new TreeMap<>(Comparator.comparing(World::getEnvironment).thenComparing(World::getName));
        plugin.getGameRegionRepository().getRegions().asMap().forEach((world, regions) -> world2regionsCount.put(world.getWorld(), regions.size()));

        var message = Component.text()
                .append(Component.text("Regions (" + plugin.getGameRegionRepository().getRegions().size() + "):", NamedTextColor.WHITE));

        world2regionsCount.forEach((world, count) ->
                message.append(
                        Component.newline(),
                        Component.text(world.getName(), NamedTextColor.GOLD),
                        Component.text(": ", NamedTextColor.GRAY),
                        Component.text(count, NamedTextColor.WHITE)
                )
        );

        commandSender.sendMessage(message);
    }

    @Subcommand("setup")
    public void setup(@NotNull CommandSender commandSender) {
        int maxRegions = MAX_REGIONS_PER_WORLD * MAX_WORLDS_PER_ENVIRONMENT;

        Set<GameRegion> regions = new HashSet<>();

        for (Environment environment : EnumSet.of(Environment.NORMAL, Environment.NETHER, Environment.THE_END)) {
            for (int i = 0; i < maxRegions; i++) {
                regions.add(plugin.getGameRegionRepository().getAndReserveRegion(environment));
            }
        }

        for (GameRegion region : regions) {
            region.setReserved(false);
        }
        commandSender.sendPlainMessage(regions.size() + " regions are available!");
    }
}
