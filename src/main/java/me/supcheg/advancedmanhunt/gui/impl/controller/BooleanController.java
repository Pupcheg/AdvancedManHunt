package me.supcheg.advancedmanhunt.gui.impl.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.Duration;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BooleanController {
    private boolean state;
    @Getter
    private int ticksUntilStateSwap;
    private boolean updated;

    public BooleanController(boolean state) {
        setState(state);
    }

    public void setState(boolean value) {
        this.state = value;
        this.ticksUntilStateSwap = Duration.INFINITY_VALUE;
        updated = true;
    }

    public void setStateFor(boolean value, int ticks) {
        this.state = value;
        this.ticksUntilStateSwap = ticks;
        updated = true;
    }

    public void tick() {
        if (ticksUntilStateSwap > Duration.INFINITY_VALUE) {
            ticksUntilStateSwap--;

            if (ticksUntilStateSwap == 0) {
                state = !state;
                updated = true;
            }
        }
    }

    public boolean getState() {
        return state;
    }

    public boolean isUpdated() {
        boolean oldValue = updated;
        updated = false;
        return oldValue;
    }
}
