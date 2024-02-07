package me.supcheg.advancedmanhunt.gui.api;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.load.PreloadedAdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.key.DefaultKeyModifier;
import me.supcheg.advancedmanhunt.gui.api.key.KeyModifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.Writer;
import java.util.Collection;
import java.util.Objects;

public interface AdvancedGuiController {
    @NotNull
    @Contract("-> new")
    AdvancedGuiBuilder gui();

    @NotNull
    @Contract("-> new")
    AdvancedButtonBuilder button();

    @UnmodifiableView
    @NotNull
    Collection<AdvancedGui> getRegisteredGuis();

    @UnmodifiableView
    @NotNull
    Collection<String> getRegisteredKeys();

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_, _ -> new")
    default AdvancedGui loadResource(@NotNull Object logicClass, @NotNull String resourcePath) {
        return loadResource(logicClass, resourcePath, DefaultKeyModifier.NO_CHANGES);
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_, _, _ -> new")
    AdvancedGui loadResource(@NotNull Object logicClass, @NotNull String resourcePath, @NotNull KeyModifier keyModifier);

    @NotNull
    @Contract("_ -> new")
    default PreloadedAdvancedGui preloadResource(@NotNull String resourcePath) {
        return preloadResource(resourcePath, DefaultKeyModifier.NO_CHANGES);
    }

    void saveResource(@NotNull AdvancedGui gui, @NotNull Writer writer);

    @NotNull
    @Contract("_, _ -> new")
    PreloadedAdvancedGui preloadResource(@NotNull String resourcePath, @NotNull KeyModifier keyModifier);

    @NotNull
    default AdvancedGui getGuiOrThrow(@NotNull String key) {
        return Objects.requireNonNull(getGui(key), "Not found gui with key=" + key);
    }

    @Nullable
    AdvancedGui getGui(@NotNull String key);

    void unregister(@NotNull String key);

    void unregister(@NotNull AdvancedGui gui);
}
