package me.supcheg.advancedmanhunt.gui.api;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface AdvancedGui {

    @NotNull
    String getKey();

    @NotNull
    AdvancedGuiController getController();


    int getRows();

    @Nullable
    @CanIgnoreReturnValue
    InventoryView open(@NotNull Player player);


    default void setBackground(@NotNull String pngSubPath) {
        Objects.requireNonNull(pngSubPath, "pngSubPath");
        setBackground(GuiBackgroundFunction.constant(pngSubPath));
    }

    void setBackground(@NotNull GuiBackgroundFunction function);

    void setAnimatedBackground(@NotNull GuiBackgroundFunction function, @NotNull Duration period);
}
