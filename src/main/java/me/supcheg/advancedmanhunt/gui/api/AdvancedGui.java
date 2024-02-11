package me.supcheg.advancedmanhunt.gui.api;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AdvancedGui {

    @NotNull
    String getKey();

    @NotNull
    AdvancedGuiController getController();

    int getRows();

    @Nullable
    @CanIgnoreReturnValue
    InventoryView open(@NotNull Player player);

    void setBackground(@NotNull String pngSubPath);

    @NotNull
    @Contract("-> new")
    AdvancedGuiBuilder toBuilder();
}
