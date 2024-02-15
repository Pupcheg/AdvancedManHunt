package me.supcheg.advancedmanhunt.config;

import io.leangen.geantyref.GenericTypeReflector;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.config.serializer.DistanceSerializer;
import me.supcheg.advancedmanhunt.config.serializer.DurationSerializer;
import me.supcheg.advancedmanhunt.config.serializer.ImmutableLocationSerializer;
import me.supcheg.advancedmanhunt.config.serializer.IntLimitSerializer;
import me.supcheg.advancedmanhunt.config.serializer.KeySerializer;
import me.supcheg.advancedmanhunt.config.serializer.SoundSerializer;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class ConfigLoader {
    private final ContainerAdapter containerAdapter;

    public void loadAndSave(@NotNull String rawPath, @NotNull Class<?> type) {
        load(rawPath, type, true);
    }

    public void load(@NotNull String rawPath, @NotNull Class<?> type) {
        load(rawPath, type, false);
    }

    @SneakyThrows
    private void load(@NotNull String rawPath, @NotNull Class<?> type, boolean save) {
        Path path = containerAdapter.resolveData(rawPath);

        ObjectMapper.Factory objectMapperFactory = ObjectMapper.factoryBuilder().build();

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .headerMode(HeaderMode.PRESET)
                .defaultOptions(options ->
                        options.serializers(builder ->
                                builder.register(new DistanceSerializer())
                                        .register(new DurationSerializer())
                                        .register(new ImmutableLocationSerializer())
                                        .register(new IntLimitSerializer())
                                        .register(new KeySerializer())
                                        .register(Sound.class, new SoundSerializer())
                                        .register(ConfigLoader::isConfigurationPart, objectMapperFactory.asTypeSerializer())
                        ).header(tryFindHeader(type))
                )
                .build();

        CommentedConfigurationNode node;
        if (Files.notExists(path)) {
            node = CommentedConfigurationNode.root(loader.defaultOptions());
        } else {
            node = loader.load();
        }

        Object instance = node.require(type);
        if (save) {
            node.set(type, instance);
            loader.save(node);
        }
    }


    private static boolean isConfigurationPart(@NotNull Type type) {
        return ConfigurationPart.class.isAssignableFrom(GenericTypeReflector.erase(type));
    }

    @SneakyThrows
    @Nullable
    private static String tryFindHeader(@NotNull Class<?> clazz) {
        try {
            MethodHandle header = MethodHandles.lookup().findStaticGetter(clazz, "HEADER", String.class);
            return (String) header.invoke();
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
