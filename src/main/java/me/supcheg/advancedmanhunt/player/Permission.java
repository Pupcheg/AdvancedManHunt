package me.supcheg.advancedmanhunt.player;

import static me.supcheg.advancedmanhunt.AdvancedManHuntPlugin.NAMESPACE;

public class Permission {
    private Permission() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String NOTIFICATIONS = NAMESPACE + ".notifications";
    public static final String TEST_COMMAND = NAMESPACE + ".test_command";
}
