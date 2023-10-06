package me.supcheg.advancedmanhunt.gui.impl.controller;

import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ResourceController<F extends Function<C, R>, C, R> {
    protected F function;
    protected int changePeriodTicks;
    protected int ticksUntilNextChange;
    @Getter
    protected R resource;
    protected boolean updated;

    public ResourceController(@NotNull F function, @NotNull Duration changePeriod) {
        setFunctionWithChangePeriod(function, changePeriod);
    }

    public void tick(@NotNull C ctx) {
        if (ticksUntilNextChange > Duration.INFINITY_VALUE) {
            ticksUntilNextChange--;

            if (ticksUntilNextChange == 0) {
                resource = function.apply(ctx);
                ticksUntilNextChange = changePeriodTicks;
                updated = true;
            }
        }
    }

    public void setFunction(@NotNull F function) {
        this.function = function;
        this.changePeriodTicks = 0;
        this.updated = false;
    }

    public void setFunctionWithChangePeriod(@NotNull F function, @NotNull Duration changePeriod) {
        this.function = function;
        this.changePeriodTicks = changePeriod.getTicks();
        this.ticksUntilNextChange = 0;
        this.updated = false;
    }

    public boolean isUpdated() {
        boolean oldValue = updated;
        updated = false;
        return oldValue;
    }
}
