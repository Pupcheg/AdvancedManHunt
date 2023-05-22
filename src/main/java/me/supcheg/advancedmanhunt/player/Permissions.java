package me.supcheg.advancedmanhunt.player;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.PLUGIN_NAME;

public class Permissions {
    private Permissions() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String NOTIFICATIONS = PLUGIN_NAME + ".notifications";
    public static final String TEMPLATE_COMMAND = PLUGIN_NAME + ".command.template";
}
