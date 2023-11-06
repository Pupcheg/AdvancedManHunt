package me.supcheg.advancedmanhunt.game;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.template.Template;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@NoArgsConstructor
@Getter
public class ManHuntGameConfiguration {
    private boolean frozen;

    private int maxHunters;
    private int maxSpectators;
    private boolean randomizeRolesOnStart;
    private Template overworldTemplate;
    private Template netherTemplate;
    private Template endTemplate;

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
    public ManHuntGameConfiguration setTemplate(@NotNull World.Environment environment, @NotNull Template template) {
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
    public ManHuntGameConfiguration setOverworldTemplate(@NotNull Template overworldTemplate) {
        Objects.requireNonNull(overworldTemplate, "overworldTemplate");
        assertNotFrozen();
        this.overworldTemplate = overworldTemplate;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setNetherTemplate(@NotNull Template netherTemplate) {
        Objects.requireNonNull(netherTemplate, "netherTemplate");
        assertNotFrozen();
        this.netherTemplate = netherTemplate;
        return this;
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    public ManHuntGameConfiguration setEndTemplate(@NotNull Template endTemplate) {
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
}
