package me.supcheg.advancedmanhunt.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("CanBeFinal")
public class AdvancedManHuntConfig {

    public static boolean ENABLE_DEBUG = false;

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Region {
        public static int MAX_REGIONS_PER_WORLD = 2;
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
            public static boolean USE_IF_DONT_KNOW_WHAT_TO_DO = false;
        }
    }
}
