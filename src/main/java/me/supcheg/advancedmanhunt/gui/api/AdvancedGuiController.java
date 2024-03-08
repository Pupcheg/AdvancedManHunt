package me.supcheg.advancedmanhunt.gui.api;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.key.DefaultKeyModifier;
import me.supcheg.advancedmanhunt.gui.api.key.KeyModifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Objects;

public interface AdvancedGuiController {
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

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> new")
    default AdvancedGui loadResource(@NotNull String resourcePath) {
        return loadResource(resourcePath, DefaultKeyModifier.NO_CHANGES);
    }

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_, _ -> new")
    AdvancedGui loadResource(@NotNull String resourcePath, @NotNull KeyModifier keyModifier);

    void saveResource(@NotNull AdvancedGui gui, @NotNull String path);

    @NotNull
    default AdvancedGui getGuiOrThrow(@NotNull String key) {
        return Objects.requireNonNull(getGui(key), "Not found gui with key=" + key);
    }

    @Nullable
    AdvancedGui getGui(@NotNull String key);

    @NotNull
    @Contract("_ -> new")
    AdvancedGui register(@NotNull AdvancedGuiBuilder builder);

    void unregister(@NotNull String key);
}
