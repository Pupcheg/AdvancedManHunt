package me.supcheg.advancedmanhunt.gui.impl.controller;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ResourceController<F extends Function<C, R>, C, R> {
    protected F function;
    @Getter
    protected R resource;
    protected boolean updated;

    public ResourceController(@NotNull F function) {
        setFunction(function);
    }

    public void tick(@NotNull C ctx) {
        if (updated) {
            resource = function.apply(ctx);
        }
    }

    public void setFunction(@NotNull F function) {
        this.function = function;
        this.updated = true;
    }

    public boolean pollUpdated() {
        boolean oldValue = updated;
        updated = false;
        return oldValue;
    }
}
