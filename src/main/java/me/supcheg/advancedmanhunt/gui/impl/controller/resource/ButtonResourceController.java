package me.supcheg.advancedmanhunt.gui.impl.controller.resource;

import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ButtonResourceController<F extends Function<ButtonResourceGetContext, R>, R>
        extends AbstractResourceController<F, ButtonResourceGetContext, R> {

    public ButtonResourceController(@NotNull F function, @NotNull Duration changePeriod) {
        super(function, changePeriod);
    }

    public void tick(@NotNull ButtonResourceGetContext ctx) {
        if (ticksUntilNextChange > Duration.INFINITY_VALUE) {
            ticksUntilNextChange--;

            if (ticksUntilNextChange == 0) {
                resource = function.apply(ctx);
                ticksUntilNextChange = changePeriodTicks;
                updated = true;
            }
        }
    }
}
