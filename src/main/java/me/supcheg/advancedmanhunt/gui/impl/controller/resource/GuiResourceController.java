package me.supcheg.advancedmanhunt.gui.impl.controller.resource;

import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class GuiResourceController<F extends Function<GuiResourceGetContext, R>, R> extends AbstractResourceController<F, GuiResourceGetContext, R>{
    public GuiResourceController(F function, Duration changePeriod) {
        super(function, changePeriod);
    }

    public void tick(DefaultAdvancedGui gui, Player player) {
        if (ticksUntilNextChange > Duration.INFINITY_VALUE) {
            ticksUntilNextChange--;

            if (ticksUntilNextChange == 0) {
                resource = function.apply(new GuiResourceGetContext(gui, player));
                ticksUntilNextChange = changePeriodTicks;
                updated = true;
            }
        }
    }
}
