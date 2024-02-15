package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface AdvancedButtonConfigurer extends Consumer<AdvancedButtonBuilder> {
    void configure(@NotNull AdvancedButtonBuilder builder);

    /**
     * @deprecated use {@link #configure(AdvancedButtonBuilder)}
     */
    @Deprecated
    @Override
    default void accept(AdvancedButtonBuilder builder) {
        configure(builder);
    }
}
