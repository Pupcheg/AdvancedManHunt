package me.supcheg.advancedmanhunt.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.Template;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

@AllArgsConstructor
public class RegionTemplateSerializer extends TypeAdapter<Template> {

    private static final String KEY = "key";
    private static final String SIDE_SIZE = "side_size";
    private static final String FOLDER = "folder";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull Template value) throws IOException {
        out.beginObject();

        out.name(KEY);
        out.value(value.getName());

        out.name(SIDE_SIZE);
        gson.toJson(value.getSideSize(), Distance.class, out);

        out.name(FOLDER);
        out.value(value.getFolder().toString());

        out.endObject();
    }

    @NotNull
    @Override
    public Template read(@NotNull JsonReader in) throws IOException {
        in.beginObject();

        String key = null;
        Distance sideSize = null;
        Path folder = null;

        while (in.hasNext()) {
            String name = in.nextName();

            switch (name) {
                case KEY -> key = in.nextString();
                case SIDE_SIZE -> sideSize = gson.fromJson(in, Distance.class);
                case FOLDER -> folder = Path.of(in.nextString());
                default -> throw new JsonIOException("Invalid token name: " + name);
            }
        }
        in.endObject();

        Validate.notNull(key);
        Validate.notNull(sideSize);
        Validate.notNull(folder);

        return new Template(key, sideSize, folder);
    }
}
