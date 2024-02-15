package me.supcheg.advancedmanhunt.game;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@NoArgsConstructor
@Getter
public class ManHuntGameConfiguration {
    private boolean frozen;

    private int maxHunters = AdvancedManHuntConfig.get().game.configDefaults.maxHunters;
    private int maxSpectators = AdvancedManHuntConfig.get().game.configDefaults.maxSpectators;
    private boolean randomizeRolesOnStart = AdvancedManHuntConfig.get().game.configDefaults.randomizeRolesOnStart;
    private String overworldTemplate = AdvancedManHuntConfig.get().game.configDefaults.overworldTemplate;
    private String netherTemplate = AdvancedManHuntConfig.get().game.configDefaults.netherTemplate;
    private String endTemplate = AdvancedManHuntConfig.get().game.configDefaults.endTemplate;

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setMaxHunters(int maxHunters) {
        assertNotFrozen();
        this.maxHunters = maxHunters;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setMaxSpectators(int maxSpectators) {
        assertNotFrozen();
        this.maxSpectators = maxSpectators;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setRandomizeRolesOnStart(boolean randomizeRolesOnStart) {
        assertNotFrozen();
        this.randomizeRolesOnStart = randomizeRolesOnStart;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_, _ -> this")
    public ManHuntGameConfiguration setTemplate(@NotNull World.Environment environment, @NotNull String template) {
        Objects.requireNonNull(environment, "environment");
        Objects.requireNonNull(template, "template");
        assertNotFrozen();

        switch (environment) {
            case NORMAL -> overworldTemplate = template;
            case NETHER -> netherTemplate = template;
            case THE_END -> endTemplate = template;
            case CUSTOM -> throw new IllegalArgumentException("CUSTOM environment");
        }

        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setOverworldTemplate(@NotNull String overworldTemplate) {
        Objects.requireNonNull(overworldTemplate, "overworldTemplate");
        assertNotFrozen();
        this.overworldTemplate = overworldTemplate;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setNetherTemplate(@NotNull String netherTemplate) {
        Objects.requireNonNull(netherTemplate, "netherTemplate");
        assertNotFrozen();
        this.netherTemplate = netherTemplate;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setEndTemplate(@NotNull String endTemplate) {
        Objects.requireNonNull(endTemplate, "endTemplate");
        assertNotFrozen();
        this.endTemplate = endTemplate;
        return this;
    }

    public void freeze() {
        this.frozen = true;
    }

    private void assertNotFrozen() {
        if (frozen) {
            throw new IllegalStateException("Config is frozen");
        }
    }

    public void merge(@NotNull ManHuntGameConfiguration other) {
        Objects.requireNonNull(other, "other");
        assertNotFrozen();

        this.maxHunters = other.maxHunters;
        this.maxSpectators = other.maxSpectators;
        this.randomizeRolesOnStart = other.randomizeRolesOnStart;
        this.overworldTemplate = other.overworldTemplate;
        this.netherTemplate = other.netherTemplate;
        this.endTemplate = other.endTemplate;
    }
}
