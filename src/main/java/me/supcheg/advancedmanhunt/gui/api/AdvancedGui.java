package me.supcheg.advancedmanhunt.gui.api;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AdvancedGui {
    int getRows();

    boolean isIndividual();

    @Nullable
    @CanIgnoreReturnValue
    InventoryView open(@NotNull Player player);


    void setBackground(@NotNull String pngSubPath);

    void animatedBackground(@NotNull String pngSubPathTemplate, int size, @NotNull Duration period);

    void lazyBackground(@NotNull GuiBackgroundFunction function);

    void lazyAnimatedBackground(@NotNull GuiBackgroundFunction function, @NotNull Duration period);
}
