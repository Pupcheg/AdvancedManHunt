package me.supcheg.advancedmanhunt.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.MessageFormat;

public class MessageFormatSerializer extends TypeAdapter<MessageFormat> {
    @Override
    public void write(@NotNull JsonWriter out, @NotNull MessageFormat value) throws IOException {
        out.value(value.toPattern());
    }

    @NotNull
    @Override
    public MessageFormat read(@NotNull JsonReader in) throws IOException {
        return new MessageFormat(in.nextString());
    }
}
