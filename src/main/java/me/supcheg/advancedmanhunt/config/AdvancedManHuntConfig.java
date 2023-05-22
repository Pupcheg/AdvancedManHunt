package me.supcheg.advancedmanhunt.config;

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
}
