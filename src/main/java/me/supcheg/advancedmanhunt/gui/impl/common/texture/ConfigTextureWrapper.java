package me.supcheg.advancedmanhunt.gui.impl.common.texture;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.Keys;
import me.supcheg.advancedmanhunt.util.reflect.Types;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ConfigTextureWrapper extends MapTextureWrapper {
    private static final Map<String, Style> STYLE_CACHE = new HashMap<>();
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
        List<FullComponentGuiTexture> textures = GSON.fromJson(jsonElement, Types.type(List.class, FullComponentGuiTexture.class));
        textures.forEach(this::addFullComponentGuiTexture);
    }

    private void loadButtons(@NotNull JsonElement jsonElement) {
        List<PaperItemTexture> textures = GSON.fromJson(jsonElement, Types.type(List.class, PaperItemTexture.class));
        textures.forEach(this::addPaperItemTexture);
    }

    private void addFullComponentGuiTexture(@NotNull FullComponentGuiTexture texture) {
        addComponentGuiTexture(texture.asComponentGuiTexture());
    }

    @NotNull
    private static Style getOrCreateStyle(@NotNull String font) {
        return STYLE_CACHE.computeIfAbsent(
                font,
                key -> Style.style()
                        .font(Keys.key(key))
                        .color(NamedTextColor.WHITE)
                        .build()
        );
    }

    @Data
    private static final class FullComponentGuiTexture {
        private final String path;
        private final String font;
        private final char altChar;
        private final int height;
        private final int width;

        @NotNull
        public ComponentGuiTexture asComponentGuiTexture() {
            return new ComponentGuiTexture(path, buildComponent(), height, width);
        }

        @NotNull
        private Component buildComponent() {
            return Component.text()
                    .content("\uF808") // 8 negative space
                    .append(Component.text(altChar, getOrCreateStyle(font)))
                    .build()
                    .compact();
        }
    }
}
