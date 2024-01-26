package me.supcheg.advancedmanhunt.gui.api.sequence;

import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import org.jetbrains.annotations.NotNull;

public interface Positionable {
    @NotNull
    At getAt();
}
