package me.supcheg.advancedmanhunt.gui.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiLoader;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;

@RequiredArgsConstructor
public class JsonGuiLoader implements AdvancedGuiLoader {
    private final ContainerAdapter containerAdapter;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new JsonGuiSerializer())
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @NotNull
    @Override
    public AdvancedGuiBuilder loadResource(@NotNull String path) throws IOException {
        try (BufferedReader in = Files.newBufferedReader(containerAdapter.resolveResource(path))) {
            return loadResource(in);
        }
    }

    @NotNull
    @Override
    public AdvancedGuiBuilder loadResource(@NotNull Reader in) {
        return gson.fromJson(in, AdvancedGuiBuilder.class);
    }

    @Override
    public void saveResource(@NotNull AdvancedGuiBuilder gui, @NotNull String path) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(containerAdapter.resolveData(path))) {
            saveResource(gui, out);
        }
    }

    @Override
    public void saveResource(@NotNull AdvancedGuiBuilder gui, @NotNull Writer out) {
        gson.toJson(gui, AdvancedGuiBuilder.class, out);
    }
}
