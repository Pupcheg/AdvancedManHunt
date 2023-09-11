package me.supcheg.advancedmanhunt.gui.impl.controller.resource;

import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.Duration;

import java.util.function.Function;

public abstract class AbstractResourceController<F extends Function<C, R>, C, R> {
    protected F function;
    protected int changePeriodTicks;
    protected int ticksUntilNextChange;
    @Getter
    protected R resource;

    public AbstractResourceController(F function, Duration changePeriod) {
        this.function = function;
        this.changePeriodTicks = changePeriod.getTicks();
        this.ticksUntilNextChange = 0;
    }

    public void setFunction(F function) {
        this.function = function;
        this.changePeriodTicks = 0;
    }

    public void setFunctionWithChangePeriod(F function, int changePeriod) {
        this.function = function;
        this.changePeriodTicks = changePeriod;
        this.ticksUntilNextChange = 0;
    }
}
