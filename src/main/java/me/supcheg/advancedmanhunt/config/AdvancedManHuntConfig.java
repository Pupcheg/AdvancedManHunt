package me.supcheg.advancedmanhunt.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.coord.Distance;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("CanBeFinal")
public final class AdvancedManHuntConfig {

    public static boolean ENABLE_DEBUG = false;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Region {
        public static int MAX_REGIONS_PER_WORLD = 4;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class TemplateLoad {
        public static int THREAD_POOL_SIZE = 4;
        public static boolean EMPTY_TEMPLATE_WARNING = true;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Game {
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class SafeLeave {
            public static boolean ENABLE = true;
            public static Duration ENABLE_AFTER = Duration.ofSeconds(30);
            public static Duration RETURN_DURATION = Duration.ofMinutes(5);
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class PlayerReturner {
            public static String TYPE = "teleport";
            public static String ARGUMENT = "world[spawn]";
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class Portal {
            public static double NETHER_MULTIPLIER = 8;
            public static Distance OVERWORLD_SAFE_ZONE = Distance.ofBlocks(192);
            public static Distance NETHER_SAFE_ZONE = Distance.ofBlocks(192);
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class DefaultConfig {
            public static int MAX_HUNTERS = 5;
            public static int MAX_SPECTATORS = 15;
            public static boolean RANDOMIZE_ROLES_ON_START = true;
            public static String OVERWORLD_TEMPLATE = AdvancedManHuntPlugin.NAMESPACE + ":default_overworld";
            public static String NETHER_TEMPLATE = AdvancedManHuntPlugin.NAMESPACE + ":default_nether";
            public static String END_TEMPLATE = AdvancedManHuntPlugin.NAMESPACE + ":default_end";
        }
    }
}
