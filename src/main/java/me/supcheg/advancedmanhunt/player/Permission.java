package me.supcheg.advancedmanhunt.player;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Permission {
    public static final String NOTIFICATIONS = "advancedmanhunt.notifications";
    public static final String CONFIGURE_ANY_GAME = "advancedmanhunt.game.configure_any";
    public static final String DEBUG = "advancedmanhunt.debug";
}
