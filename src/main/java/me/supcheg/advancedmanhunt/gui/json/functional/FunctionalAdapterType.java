package me.supcheg.advancedmanhunt.gui.json.functional;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface FunctionalAdapterType<I> {
    boolean canWrite(@NotNull Object obj);

    void write(@NotNull JsonWriter out, @NotNull I value) throws IOException;

    @NotNull
    I read(@NotNull JsonReader in) throws IOException;
}