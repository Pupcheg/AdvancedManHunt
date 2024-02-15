package me.supcheg.advancedmanhunt.config.serializer;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public class SoundSerializer implements TypeSerializer<Sound> {
    private static final String KEY = "key";
    private static final String SOURCE = "source";
    private static final String VOLUME = "volume";
    private static final String PITCH = "pitch";

    @Override
    public Sound deserialize(Type type, @NotNull ConfigurationNode src) throws SerializationException {
        if (!src.isMap()) {
            Key key = src.get(Key.class);
            if (key != null) {
                return Sound.sound(key, Sound.Source.MASTER, 1, 1);
            }
        }

        Key key = src.node(KEY).get(Key.class);
        Sound.Source source = src.node(SOURCE).get(Sound.Source.class, Sound.Source.MASTER);
        float volume = src.node(VOLUME).getFloat(1);
        float pitch = src.node(PITCH).getFloat(1);

        Objects.requireNonNull(key, KEY);
        Objects.requireNonNull(source, SOURCE);

        return Sound.sound(key, source, volume, pitch);
    }

    @Override
    public void serialize(Type type, @Nullable Sound obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set("null");
            return;
        }

        String key = node.getString();
        if (key != null) {
            node.set(key);
            return;
        }

        node.node(KEY).set(obj.name());
        node.node(SOURCE).set(obj.source());
        node.node(VOLUME).set(obj.volume());
        node.node(PITCH).set(obj.pitch());
    }
}
