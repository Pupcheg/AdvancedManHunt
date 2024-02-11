package me.supcheg.advancedmanhunt.gui.impl.common;

public class BooleanController {
    private final boolean initialState;
    private boolean state;
    private boolean updated;

    public BooleanController(boolean state) {
        setState(state);
        this.initialState = state;
    }

    public void setState(boolean value) {
        if (state == value) {
            return;
        }

        this.state = value;
        this.updated = true;
    }

    public boolean getState() {
        return state;
    }

    public boolean getInitialState() {
        return initialState;
    }

    public boolean pollUpdated() {
        boolean oldValue = updated;
        updated = false;
        return oldValue;
    }
}
