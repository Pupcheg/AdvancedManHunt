package me.supcheg.advancedmanhunt.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Distance;

import java.time.Duration;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("CanBeFinal")
public final class AdvancedManHuntConfig {

    public static boolean ENABLE_DEBUG = false;

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Region {
        public static int MAX_REGIONS_PER_WORLD = 4;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class TemplateLoad {
        public static int THREAD_POOL_SIZE = 4;
        public static boolean EMPTY_TEMPLATE_WARNING = true;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Serialization {
        public static boolean COMPACT_COORDS = true;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Game {
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class SafeLeave {
            public static boolean ENABLE = true;
            public static Duration ENABLE_AFTER = Duration.ofSeconds(30);
            public static Duration RETURN_DURATION = Duration.ofMinutes(5);
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class PlayerReturner {
            public static String TYPE = "teleport";
            public static String ARGUMENT = "world[spawn]";
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class Portal {
            public static double NETHER_MULTIPLIER = 8;
            public static Distance OVERWORLD_SAFE_ZONE = Distance.ofBlocks(192);
            public static Distance NETHER_SAFE_ZONE = Distance.ofBlocks(192);
        }
    }
}
