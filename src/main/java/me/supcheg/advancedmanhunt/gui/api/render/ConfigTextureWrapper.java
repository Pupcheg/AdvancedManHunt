package me.supcheg.advancedmanhunt.gui.api.render;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings("PatternValidation")
@RequiredArgsConstructor
public class ConfigTextureWrapper extends MapTextureWrapper {
    private static final Function<String, Style> STYLE_BUILDER = key -> Style.style()
            .font(Key.key(key))
            .color(NamedTextColor.WHITE)
            .build();
    private static final Map<String, Style> STYLE_CACHE = new ConcurrentHashMap<>();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private final ContainerAdapter containerAdapter;

    @SneakyThrows
    public void load(@NotNull String resourcePath) {
        Path path = containerAdapter.resolveResource(resourcePath);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            load(reader);
        }
    }

    public void load(@NotNull Reader reader) {
        JsonObject object = GSON.fromJson(reader, JsonObject.class);
        loadGuis(object.get("guis"));
        loadButtons(object.get("buttons"));
    }

    private void loadGuis(@NotNull JsonElement jsonElement) {
        List<GuiTexture> guis = GSON.fromJson(jsonElement, Types.type(List.class, GuiTexture.class));
        for (GuiTexture gui : guis) {
            Component component = Component.text('\uF808')
                    .append(Component.text(gui.getAltChar(), getStyle(gui.getFont())))
                    .compact();
            putGui(gui.getKey(), component);
        }
    }

    private void loadButtons(@NotNull JsonElement jsonElement) {
        List<ButtonTexture> buttons = GSON.fromJson(jsonElement, Types.type(List.class, ButtonTexture.class));
        for (ButtonTexture button : buttons) {
            putButton(button.getKey(), button.getCustomModelData());
        }
    }

    private Style getStyle(@NotNull String font) {
        return STYLE_CACHE.computeIfAbsent(font, STYLE_BUILDER);
    }

    @Data
    private static final class GuiTexture {
        private final String key;
        private final String font;
        private final char altChar;
    }

    @Data
    private static final class ButtonTexture {
        private final String key;
        private final int customModelData;
    }
}
