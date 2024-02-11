package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.functional.action.PlaySoundButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@RequiredArgsConstructor
public class PlaySoundButtonClickActionConsumerType implements FunctionalAdapterType<PlaySoundButtonClickActionConsumer> {
    public static final String NAME = "sound";
    private static final String SOUND = "sound";

    private final Gson gson;

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof PlaySoundButtonClickActionConsumer;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull PlaySoundButtonClickActionConsumer value) throws IOException {
        gson.toJson(value.getSound(), Sound.class, out);
    }

    @NotNull
    @Override
    public PlaySoundButtonClickActionConsumer read(@NotNull JsonReader in) throws IOException {
        Sound sound = null;

        if (in.nextName().equals(SOUND)) {
            sound = gson.fromJson(in, Sound.class);
        }

        PropertyHelper.assertNonNull(sound, SOUND, in);

        return new PlaySoundButtonClickActionConsumer(sound);
    }
}
