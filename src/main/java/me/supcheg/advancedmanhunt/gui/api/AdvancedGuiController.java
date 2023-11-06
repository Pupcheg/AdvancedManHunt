package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface AdvancedGuiController {
    @NotNull
    @Contract("-> new")
    AdvancedGuiBuilder gui();

    @NotNull
    @Contract("-> new")
    AdvancedButtonBuilder button();


    @NotNull
    default AdvancedGui getGuiOrThrow(@NotNull String key) {
        return Objects.requireNonNull(getGui(key), "Not found gui with key=" + key);
    }

    @Nullable
    AdvancedGui getGui(@NotNull String key);

    @NotNull
    @Contract("_ -> new")
    AdvancedGui buildAndRegister(@NotNull AdvancedGuiBuilder builder);

    void unregister(@NotNull String key);

    void unregister(@NotNull AdvancedGui gui);
}
