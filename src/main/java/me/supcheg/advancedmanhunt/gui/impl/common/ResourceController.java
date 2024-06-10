package me.supcheg.advancedmanhunt.gui.impl.common;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public final class ResourceController<R> {

    @Getter
    private final R initialResource;
    @Getter
    private R resource;
    private boolean updated;

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
