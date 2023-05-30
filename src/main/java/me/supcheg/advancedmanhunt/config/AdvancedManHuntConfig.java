package me.supcheg.advancedmanhunt.config;

import java.time.Duration;

public class AdvancedManHuntConfig {

    public static boolean ENABLE_DEBUG = false;

    public static final class Region {
        public static int MAX_REGIONS_PER_WORLD = 2;
        public static int MAX_WORLDS_PER_ENVIRONMENT = 1;
    }

    public static final class TemplateLoad {
        public static int THREAD_POOL_SIZE = 4;
        public static boolean EMPTY_TEMPLATE_WARNING = true;
    }

    public static final class Serialization {
        public static boolean COMPACT_COORDS = true;
    }

    public static final class Game {
        public static final class SafeLeave {
            public static boolean ENABLE = true;
            public static Duration DURATION = Duration.ofSeconds(30);
            public static Duration RETURN_DURATION = Duration.ofMinutes(5);
        }

        public static final class PlayerReturner {
            public static String TYPE = "teleport";
            public static String ARGUMENT = "world[spawn]";
        }
    }
}
