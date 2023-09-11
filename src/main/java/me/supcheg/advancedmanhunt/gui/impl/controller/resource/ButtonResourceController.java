package me.supcheg.advancedmanhunt.gui.impl.controller.resource;

import it.unimi.dsi.fastutil.ints.IntSet;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedButton;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class ButtonResourceController<F extends Function<ButtonResourceGetContext, R>, R>
        extends AbstractResourceController<F, ButtonResourceGetContext, R> {

    public ButtonResourceController(F function, Duration changePeriod) {
        super(function, changePeriod);
    }

    public void tick(DefaultAdvancedButton button, IntSet buttonSlots, Player player) {
        if (ticksUntilNextChange > Duration.INFINITY_VALUE) {
            ticksUntilNextChange--;

            if (ticksUntilNextChange == 0) {
                resource = function.apply(new ButtonResourceGetContext(button.getGui(), button, buttonSlots, player));
                ticksUntilNextChange = changePeriodTicks;
            }
        }
    }
}
