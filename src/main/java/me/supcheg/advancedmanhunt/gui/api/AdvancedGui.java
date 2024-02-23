package me.supcheg.advancedmanhunt.gui.api;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface AdvancedGui {

    @NotNull
    String getKey();

    @NotNull
    AdvancedGuiController getController();

    int getRows();

    @CanIgnoreReturnValue
    boolean open(@NotNull Player player);

    void setBackground(@NotNull String pngSubPath);

    @NotNull
    String getBackground();

    @NotNull
    Collection<GuiTicker> getTickers();

    @NotNull
    @Contract("-> new")
    AdvancedGuiBuilder toBuilder();
}
