package me.supcheg.advancedmanhunt.gui.impl.controller;

import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.Duration;

@Getter
public class BooleanController {
    private boolean state;
    private int ticksUntilStateSwap;

    public void setState(boolean value) {
        this.state = value;
        this.ticksUntilStateSwap = Duration.INFINITY_VALUE;
    }

    public void setStateFor(boolean value, int ticks) {
        this.state = value;
        this.ticksUntilStateSwap = ticks;
    }

    public void tick() {
        if (ticksUntilStateSwap > Duration.INFINITY_VALUE) {
            ticksUntilStateSwap--;
            System.out.println(ticksUntilStateSwap);

            if (ticksUntilStateSwap == 0) {
                state = !state;
            }
        }
    }
}
