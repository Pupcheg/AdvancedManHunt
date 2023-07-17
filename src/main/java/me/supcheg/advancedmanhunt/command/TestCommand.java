package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.player.Permission;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TestCommand extends AbstractCommand {
    private final AdvancedManHuntPlugin plugin;

    private RegionPortalHandler portalHandler;

    public TestCommand(@NotNull AdvancedManHuntPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(@NotNull CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher) {
        commandDispatcher.register(
                literal("test")
                        .requires(src -> src.getBukkitSender().hasPermission(Permission.TEST_COMMAND))
                        .then(literal("portal")
                                .then(literal("handle").executes(this::handle))
                                .then(literal("close").executes(this::close))
                        )
        );
    }

    private int handle(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        if (portalHandler != null) {
            return 0;
        }

        GameRegionRepository gameRegionRepository = plugin.getGameRegionRepository();

        GameRegion overworld = gameRegionRepository.getRegion(World.Environment.NORMAL);

        portalHandler = new RegionPortalHandler(
                gameRegionRepository,
                plugin.getPlayerViewRepository(),
                plugin.getPlayerReturner(),
                overworld,
                gameRegionRepository.getRegion(World.Environment.NETHER),
                gameRegionRepository.getRegion(World.Environment.THE_END)
        );

        Player player = (Player) ctx.getSource().getBukkitEntity();
        Objects.requireNonNull(player);

        Location location = overworld.getCenterBlock().asLocation(overworld.getWorld(), 65);

        Location clone = location.clone();
        clone.setY(64);
        overworld.getWorld().getBlockAt(clone).setType(Material.GRASS_BLOCK);

        player.teleport(location);

        return Command.SINGLE_SUCCESS;
    }

    private int close(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        if (portalHandler == null) {
            return 0;
        }

        portalHandler.close();
        portalHandler = null;

        return Command.SINGLE_SUCCESS;
    }
}
