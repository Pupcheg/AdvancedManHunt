package me.supcheg.advancedmanhunt.game;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@NoArgsConstructor
public final class ManHuntGameConfiguration {
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
    public ManHuntGameConfiguration maxHunters(int maxHunters) {
        assertNotFrozen();
        this.maxHunters = maxHunters;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration maxSpectators(int maxSpectators) {
        assertNotFrozen();
        this.maxSpectators = maxSpectators;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration randomizeRolesOnStart(boolean randomizeRolesOnStart) {
        assertNotFrozen();
        this.randomizeRolesOnStart = randomizeRolesOnStart;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_, _ -> this")
    public ManHuntGameConfiguration template(@NotNull RealEnvironment environment, @NotNull Key key) {
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

    public void freeze() {
        this.frozen = true;
    }

    public void unfreeze() {
        this.frozen = false;
    }

    private void assertNotFrozen() {
        if (frozen) {
            throw new IllegalStateException("Config is frozen");
        }
    }

    public void mergeFrom(@NotNull ManHuntGameConfiguration other) {
        Objects.requireNonNull(other, "other");
        assertNotFrozen();

        this.maxHunters = other.maxHunters;
        this.maxSpectators = other.maxSpectators;
        this.randomizeRolesOnStart = other.randomizeRolesOnStart;
        this.overworldTemplate = other.overworldTemplate;
        this.netherTemplate = other.netherTemplate;
        this.endTemplate = other.endTemplate;
    }

    public boolean frozen() {
        return this.frozen;
    }

    public int maxHunters() {
        return this.maxHunters;
    }

    public int maxSpectators() {
        return this.maxSpectators;
    }

    public boolean randomizeRolesOnStart() {
        return this.randomizeRolesOnStart;
    }

    @NotNull
    public Key overworldTemplate() {
        return this.overworldTemplate;
    }

    @NotNull
    public Key netherTemplate() {
        return this.netherTemplate;
    }

    @NotNull
    public Key endTemplate() {
        return this.endTemplate;
    }
}
