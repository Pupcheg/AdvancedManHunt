package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.util.JsonReaders;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@RequiredArgsConstructor
public class AdvancedSoundAdapter extends TypeAdapter<Sound> {
    private static final Sound.Source DEFAULT_SOURCE = Sound.Source.MASTER;
    private static final float DEFAULT_VOLUME = 1;
    private static final float DEFAULT_PITCH = 1;

    private static final String KEY = "key";
    private static final String SOURCE = "source";
    private static final String VOLUME = "volume";
    private static final String PITCH = "pitch";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull Sound value) throws IOException {
        if (value.source() == DEFAULT_SOURCE && value.volume() == DEFAULT_VOLUME && value.pitch() == DEFAULT_PITCH) {
            out.value(value.name().asString());
        } else {
            out.beginObject();
        }
    }

    @NotNull
    @Override
    public Sound read(@NotNull JsonReader in) throws IOException {
        Key key = null;
        Sound.Source source = DEFAULT_SOURCE;
        float volume = DEFAULT_VOLUME;
        float pitch = DEFAULT_PITCH;

        if (in.peek() == JsonToken.STRING) {
            key = gson.fromJson(in, Key.class);
        } else {
            in.beginObject();

            while (in.hasNext()) {
                String name = JsonReaders.nextNonDollarName(in);
                switch (name) {
                    case KEY -> key = gson.fromJson(in, Key.class);
                    case SOURCE -> source = gson.fromJson(in, Sound.Source.class);
                    case VOLUME -> volume = (float) in.nextDouble();
                    case PITCH -> pitch = (float) in.nextDouble();
                    default -> throw PropertyHelper.unknownNameException(name, in);
                }
            }

            in.endObject();
        }

        PropertyHelper.assertNonNull(key, KEY, in);
        PropertyHelper.assertNonNull(source, SOURCE, in);

        return Sound.sound(key, source, volume, pitch);
    }
}
