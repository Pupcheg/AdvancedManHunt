package me.supcheg.advancedmanhunt.game;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@NoArgsConstructor
@Getter
public class ManHuntGameConfiguration {
    private boolean frozen;

    private int maxHunters = config().game.configDefaults.maxHunters;
    private int maxSpectators = config().game.configDefaults.maxSpectators;
    private boolean randomizeRolesOnStart = config().game.configDefaults.randomizeRolesOnStart;
    private Key overworldTemplate = config().game.configDefaults.overworldTemplate;
    private Key netherTemplate = config().game.configDefaults.netherTemplate;
    private Key endTemplate = config().game.configDefaults.endTemplate;

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
    public ManHuntGameConfiguration setTemplate(@NotNull RealEnvironment environment, @NotNull Key key) {
        Objects.requireNonNull(environment, "environment");
        Objects.requireNonNull(key, "key");
        assertNotFrozen();

        switch (environment) {
            case OVERWORLD -> overworldTemplate = key;
            case NETHER -> netherTemplate = key;
            case THE_END -> endTemplate = key;
        }

        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setOverworldTemplate(@NotNull Key key) {
        return setTemplate(RealEnvironment.OVERWORLD, key);
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setNetherTemplate(@NotNull Key key) {
        return setTemplate(RealEnvironment.NETHER, key);
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setEndTemplate(@NotNull Key key) {
        return setTemplate(RealEnvironment.THE_END, key);
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
