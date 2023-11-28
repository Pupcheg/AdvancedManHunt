package me.supcheg.advancedmanhunt.player;

public class Permission {
    private Permission() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String NOTIFICATIONS = "advancedmanhunt.notifications";
    public static final String CONFIGURE_ANY_GAME = "advancedmanhunt.game.configure_any";
    public static final String DEBUG = "advancedmanhunt.debug";
}
