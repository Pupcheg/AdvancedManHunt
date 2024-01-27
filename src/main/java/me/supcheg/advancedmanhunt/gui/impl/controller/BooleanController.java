package me.supcheg.advancedmanhunt.gui.impl.controller;

public class BooleanController {
    private boolean state;
    private boolean updated;

    public BooleanController(boolean state) {
        setState(state);
    }

    public void setState(boolean value) {
        this.state = value;
        this.updated = true;
    }

    public boolean getState() {
        return state;
    }

    public boolean pollUpdated() {
        boolean oldValue = updated;
        updated = false;
        return oldValue;
    }
}
