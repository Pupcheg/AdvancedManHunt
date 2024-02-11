package me.supcheg.advancedmanhunt.gui.impl.controller;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class ResourceController<R> {

    @Getter
    protected final R initialResource;
    @Getter
    protected R resource;
    protected boolean updated;

    public ResourceController(@NotNull R resource) {
        setResource(resource);
        this.initialResource = resource;
    }

    public void setResource(@NotNull R resource) {
        this.resource = resource;
        this.updated = true;
    }

    public boolean pollUpdated() {
        boolean oldValue = updated;
        updated = false;
        return oldValue;
    }
}
