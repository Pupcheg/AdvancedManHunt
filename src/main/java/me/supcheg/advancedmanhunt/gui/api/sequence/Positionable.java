package me.supcheg.advancedmanhunt.gui.api.sequence;

import org.jetbrains.annotations.NotNull;

public interface Positionable {
    @NotNull
    At getAt();

    @NotNull
    Priority getPriority();
}
