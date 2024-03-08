package me.supcheg.advancedmanhunt.config;

import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.util.reflect.ReflectCalled;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;

import java.time.Duration;

@SuppressWarnings("CanBeFinal")
public class AdvancedManHuntConfig implements ConfigurationPart {
    private static AdvancedManHuntConfig INSTANCE = new AdvancedManHuntConfig();

    @ReflectCalled
    public static final String HEADER = """
            AdvancedManHunt plugin configuration
            For more information you can visit
            github: https://github.com/Pupcheg/AdvancedManHunt
            """;

    @NotNull
    public static AdvancedManHuntConfig get() {
        return INSTANCE;
    }

    @PostProcess
    public void postProcess() {
        INSTANCE = this;
    }

    @Comment("Enable debug command and prints to the console (default: false)")
    public boolean debug = false;

    public Region region = new Region();

    public static class Region implements ConfigurationPart {
        @Comment("Max regions count in single world (default: 4)")
        public int maxRegionsPerWorld = 4;
    }

    public TemplateLoad templateLoad = new TemplateLoad();

    public static class TemplateLoad implements ConfigurationPart {
        @Comment("Max thread pool size for async template load (default: 4)")
        public int threadPoolSize = 4;
        @Comment("Warn in the console if an empty template was loaded (default: true)")
        public boolean emptyTemplateWarning = true;
    }

    public Game game = new Game();

    public static class Game implements ConfigurationPart {
        @Comment("Safe leave allows the player to quit the server for a specified period and the game will not be ended")
        public SafeLeave safeLeave;

        public static class SafeLeave implements ConfigurationPart {
            @Comment("Enable safe leave (default: true)")
            public boolean enable = true;
            @Comment("How long after the start of the game will safe leave be enabled (default: 30s)")
            public Duration enableAfter = Duration.ofSeconds(30);
            @Comment("How long can a player leave the server (default: 5m)")
            public Duration returnDuration = Duration.ofMinutes(5);
        }

        public PlayerReturner playerReturner = new PlayerReturner();

        public static class PlayerReturner implements ConfigurationPart {
            @Comment("Player returner type (default: teleport)")
            public String type = "teleport";
            @Comment("Player returner config (default: world[spawn])")
            public String argument = "world[spawn]";
        }

        public Portal portal = new Portal();

        public static class Portal implements ConfigurationPart {
            @Comment("Coordinate multiplier when teleporting from nether to overworld (default: 8)")
            public double netherMultiplier = 8;
            @Comment("The distance from the edge of the region in overworld where the portal will not be able to appear")
            public Distance overworldSafeZone = Distance.ofBlocks(192);
            @Comment("The distance from the edge of the region in nether where the portal will not be able to appear")
            public Distance netherSafeZone = Distance.ofBlocks(192);
        }

        @Comment("Default ManHunt game configuration")
        public ConfigDefaults configDefaults = new ConfigDefaults();

        public static class ConfigDefaults implements ConfigurationPart {
            public int maxHunters = 5;
            public int maxSpectators = 15;
            public boolean randomizeRolesOnStart = true;
            public String overworldTemplate = "default_overworld";
            public String netherTemplate = "default_nether";
            public String endTemplate = "default_end";
        }

        @Comment("Limits in ManHunt game configuration")
        public ConfigLimits configLimits = new ConfigLimits();

        public static class ConfigLimits implements ConfigurationPart {
            public IntLimit maxHunters = IntLimit.of(1, 5);
            public IntLimit maxSpectators = IntLimit.of(0, 30);
        }
    }
}
