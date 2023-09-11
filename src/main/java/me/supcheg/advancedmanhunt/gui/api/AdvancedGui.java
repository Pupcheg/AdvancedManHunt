package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public interface AdvancedGui {
    int getRows();

    boolean isIndividual();

    InventoryView open(Player player);


    void addButton(AdvancedButtonBuilder button);

    void removeButton(int slot);

    AdvancedButton getButtonAt(int slot);


    void setBackground(String pngSubPath);

    void animatedBackground(String pngSubPathTemplate, int size, Duration period);

    void lazyBackground(GuiBackgroundFunction function);

    void lazyAnimatedBackground(GuiBackgroundFunction function, Duration period);
}
