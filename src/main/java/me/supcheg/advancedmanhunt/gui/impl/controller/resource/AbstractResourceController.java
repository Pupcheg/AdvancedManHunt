package me.supcheg.advancedmanhunt.gui.impl.controller.resource;

import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class AbstractResourceController<F extends Function<C, R>, C, R> {
    protected F function;
    protected int changePeriodTicks;
    protected int ticksUntilNextChange;
    @Getter
    protected R resource;
    protected boolean updated;

    public AbstractResourceController(@NotNull F function, @NotNull Duration changePeriod) {
        setFunctionWithChangePeriod(function, changePeriod.getTicks());
    }

    public void setFunction(@NotNull F function) {
        this.function = function;
        this.changePeriodTicks = 0;
        this.updated = true;
    }

    public void setFunctionWithChangePeriod(@NotNull F function, int changePeriod) {
        this.function = function;
        this.changePeriodTicks = changePeriod;
        this.ticksUntilNextChange = 0;
        this.updated = true;
    }

    public boolean isUpdated() {
        boolean oldValue = updated;
        updated = false;
        return oldValue;
    }
}
