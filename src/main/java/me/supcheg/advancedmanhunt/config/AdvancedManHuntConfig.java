package me.supcheg.advancedmanhunt.config;

import me.supcheg.advancedmanhunt.config.extension.Header;
import me.supcheg.advancedmanhunt.math.distance.Distance;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;

import java.time.Duration;

import static me.supcheg.advancedmanhunt.util.Keys.advancedmanhuntKey;

@SuppressWarnings("CanBeFinal")
@ConfigSerializable
public final class AdvancedManHuntConfig {
    private static AdvancedManHuntConfig INSTANCE = new AdvancedManHuntConfig();

    @Header
    public static final String HEADER = """
            AdvancedManHunt plugin configuration
            For more information you can visit
            github: https://github.com/Pupcheg/AdvancedManHunt
            """;

    @NotNull
    public static AdvancedManHuntConfig config() {
        return INSTANCE;
    }

    @PostProcess
    void postProcess() {
        INSTANCE = this;
    }

    @Comment("Enable debug command and prints to the console (default: false)")
    public boolean debug = false;

    public Region region = new Region();

    @ConfigSerializable
    public final static class Region {
        @Comment("Max regions count in single world (default: 4)")
        public int maxRegionsPerWorld = 4;
    }

    public TemplateLoad templateLoad = new TemplateLoad();

    @ConfigSerializable
    public final static class TemplateLoad {
        @Comment("Max thread pool size for async template load (default: 4)")
        public int threadPoolSize = 4;
        @Comment("Warn in the console if an empty template was loaded (default: true)")
        public boolean emptyTemplateWarning = true;
    }

    public Game game = new Game();

    @ConfigSerializable
    public final static class Game {
        @Comment("Safe leave allows the player to quit the server for a specified period and the game will not be ended")
        public SafeLeave safeLeave;

        @ConfigSerializable
        public final static class SafeLeave {
            @Comment("Enable safe leave (default: true)")
            public boolean enable = true;
            @Comment("How long after the start of the game will safe leave be enabled (default: 30s)")
            public Duration enableAfter = Duration.ofSeconds(30);
            @Comment("How long can a player leave the server (default: 5m)")
            public Duration returnDuration = Duration.ofMinutes(5);
        }

        public PlayerReturner playerReturner = new PlayerReturner();

        @ConfigSerializable
        public final static class PlayerReturner {
            @Comment("Player returner type (default: teleport)")
            public String type = "teleport";
            @Comment("Player returner config (default: world[spawn])")
            public String argument = "world[spawn]";
        }

        public Portal portal = new Portal();

        @ConfigSerializable
        public final static class Portal {
            @Comment("Coordinate multiplier when teleporting from nether to overworld (default: 8)")
            public double netherMultiplier = 8;
            @Comment("The distance from the edge of the region in overworld where the portal will not be able to appear")
            public Distance overworldSafeZone = Distance.ofBlocks(192);
            @Comment("The distance from the edge of the region in nether where the portal will not be able to appear")
            public Distance netherSafeZone = Distance.ofBlocks(192);
        }

        @Comment("Default ManHunt game configuration")
        public ConfigDefaults configDefaults = new ConfigDefaults();

        @ConfigSerializable
        public final static class ConfigDefaults {
            public int maxHunters = 5;
            public int maxSpectators = 15;
            public boolean randomizeRolesOnStart = true;
            public Key overworldTemplate = advancedmanhuntKey("default");
            public Key netherTemplate = advancedmanhuntKey("default_nether");
            public Key endTemplate = advancedmanhuntKey("default_the_end");
        }

        @Comment("Limits in ManHunt game configuration")
        public ConfigLimits configLimits = new ConfigLimits();

        @ConfigSerializable
        public final static class ConfigLimits {
            public IntLimit maxHunters = IntLimit.of(1, 5);
            public IntLimit maxSpectators = IntLimit.of(0, 30);
        }
    }
}
