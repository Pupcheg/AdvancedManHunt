package me.supcheg.advancedmanhunt.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.config.extension.Header;
import me.supcheg.advancedmanhunt.config.serializer.DistanceSerializer;
import me.supcheg.advancedmanhunt.config.serializer.DurationSerializer;
import me.supcheg.advancedmanhunt.config.serializer.IntLimitSerializer;
import me.supcheg.advancedmanhunt.config.serializer.KeySerializer;
import me.supcheg.advancedmanhunt.config.serializer.SoundSerializer;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.PositionAdapters;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class ConfigLoader {
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

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .headerMode(HeaderMode.PRESET)
                .defaultOptions(options ->
                        options.serializers(builder ->
                                builder.register(new DistanceSerializer())
                                        .register(new DurationSerializer())
                                        .register(PositionAdapters.immutableLocation().asConfigurateSerializer())
                                        .register(new IntLimitSerializer())
                                        .register(new KeySerializer())
                                        .register(Sound.class, new SoundSerializer())
                                        .registerAnnotatedObjects(ObjectMapper.factoryBuilder().build())
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

    @SneakyThrows
    @Nullable
    private static String tryFindHeader(@NotNull Class<?> clazz) {
        List<Field> headers = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Header.class))
                .toList();

        return switch (headers.size()) {
            case 0 -> null;
            case 1 -> {
                Field field = headers.getFirst();
                field.setAccessible(true);
                yield (String) field.get(null);
            }
            default -> throw new IllegalStateException("Found more than one header at: " + clazz);
        };
    }
}
