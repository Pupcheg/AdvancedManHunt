package me.supcheg.advancedmanhunt.injector;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface Bridge {
    void registerBrigadierCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> command);

    void sendTitle(@NotNull InventoryView inventoryView, @NotNull Component title);

    void writePositionsToRegion(@NotNull Path regionPath);

    @NotNull
    ItemStackWrapperFactory getItemStackWrapperFactory();
}
