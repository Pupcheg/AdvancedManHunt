package me.supcheg.advancedmanhunt.config;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanLists;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.LocationParser;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

@SuppressWarnings("PatternValidation")
public class ConfigLoader {
    private static final CustomLogger LOGGER = CustomLogger.getLogger(ConfigLoader.class);

    private final ContainerAdapter containerAdapter;
    private final Map<Class<?>, GetValueFunction<?>> type2function = new HashMap<>();

    public ConfigLoader(@NotNull ContainerAdapter containerAdapter) {
        this.containerAdapter = containerAdapter;

        register(String.class, (config, path, def) -> {
            String out;
            List<String> list = config.getStringList(path);
            if (list.isEmpty()) {
                out = config.getString(path, def);
            } else {
                out = String.join("\n", list);
            }
            return out;
        });

        register(Component.class, (config, path, def) -> {
            String serialized = config.getString(path);

            return serialized == null ? def : MiniMessage.miniMessage().deserialize(serialized)
                    .decoration(TextDecoration.ITALIC, false).compact();
        });

        register(Sound.class, (config, path, def) -> {
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section == null) {
                String rawKey = config.getString(path);
                if (rawKey == null) {
                    return def;
                }

                return Sound.sound(Key.key(rawKey), Sound.Source.MASTER, 1, 1);
            }

            Key key = Key.key(Objects.requireNonNull(section.getString("key"), "key"));
            Sound.Source source = Sound.Source.valueOf(section.getString("source", "master").toUpperCase());
            float volume = (float) section.getDouble("volume", 1);
            float pitch = (float) section.getDouble("pitch", 1);

            return Sound.sound(key, source, volume, pitch);
        });

        Pattern durationPattern = Pattern.compile("\\d+[dhms]", Pattern.CASE_INSENSITIVE);
        register(Duration.class, (config, path, def) -> {
            String serialized = config.getString(path);
            if (serialized == null) {
                return def;
            }
            if (!durationPattern.matcher(serialized).matches()) {
                throw new IllegalArgumentException("'%s' is not a valid duration".formatted(serialized));
            }

            int duration = Integer.parseInt(serialized.substring(0, serialized.length() - 1));
            return switch (Character.toLowerCase(serialized.charAt(serialized.length() - 1))) {
                case 'd' -> Duration.ofDays(duration);
                case 'h' -> Duration.ofHours(duration);
                case 'm' -> Duration.ofMinutes(duration);
                case 's' -> Duration.ofSeconds(duration);
                default -> throw new IllegalStateException("Unreachable");
            };
        });

        register(Location.class, (config, path, def) -> {
            String serialized = config.getString(path);
            if (serialized == null) {
                return def;
            }
            return LocationParser.parseLocation(serialized);
        });

        Pattern distancePattern = Pattern.compile("\\d+[bcr]", Pattern.CASE_INSENSITIVE);
        register(Distance.class, (config, path, def) -> {
            String serialized = config.getString(path);
            if (serialized == null) {
                return def;
            }
            if (!distancePattern.matcher(serialized).matches()) {
                throw new IllegalArgumentException("'%s' is not a valid distance".formatted(serialized));
            }

            int distance = Integer.parseInt(serialized.substring(0, serialized.length() - 1));
            return switch (Character.toLowerCase(serialized.charAt(serialized.length() - 1))) {
                case 'b' -> Distance.ofBlocks(distance);
                case 'c' -> Distance.ofChunks(distance);
                case 'r' -> Distance.ofRegions(distance);
                default -> throw new IllegalStateException("Unreachable");
            };
        });

        register(int.class, (config, path, def) -> def == null ? config.getInt(path) : config.getInt(path, def));
        register(long.class, (config, path, def) -> def == null ? config.getLong(path) : config.getLong(path, def));
        register(double.class, (config, path, def) -> def == null ? config.getDouble(path) : config.getDouble(path, def));
        register(boolean.class, (config, path, def) -> def == null ? config.getBoolean(path) : config.getBoolean(path, def));

        register(List.class, list(FileConfiguration::getList, UnaryOperator.identity(), Collections::unmodifiableList));

        register(IntList.class, list(FileConfiguration::getIntegerList, IntArrayList::new, IntLists::unmodifiable));
        register(LongList.class, list(FileConfiguration::getLongList, LongArrayList::new, LongLists::unmodifiable));
        register(DoubleList.class, list(FileConfiguration::getDoubleList, DoubleArrayList::new, DoubleLists::unmodifiable));
        register(BooleanList.class, list(FileConfiguration::getBooleanList, BooleanArrayList::new, BooleanLists::unmodifiable));
    }

    public <T> void register(@NotNull Class<T> clazz,
                             @NotNull GetValueFunction<? extends T> function) {
        type2function.put(clazz, function);
    }

    @NotNull
    @Contract
    public static <P, B> GetValueFunction<P> list(@NotNull BiFunction<FileConfiguration, String, List<B>> getList,
                                                  @NotNull Function<List<B>, P> toPrimitiveList,
                                                  @NotNull UnaryOperator<P> toUnmodifiable) {
        return (config, path, def) -> {
            List<B> list = getList.apply(config, path);
            if (list.isEmpty()) {
                return def;
            } else {
                return toUnmodifiable.apply(toPrimitiveList.apply(list));
            }
        };
    }

    public void load(@NotNull String resourceName, @NotNull Class<?> configClass) {
        LOGGER.debugIfEnabled("Loading {} class from {}", configClass.getSimpleName(), resourceName);
        Path path = containerAdapter.unpackResource(resourceName);

        YamlConfiguration yamlConfiguration = new YamlConfiguration();

        try (Reader reader = Files.newBufferedReader(path)) {
            yamlConfiguration.load(reader);
        } catch (IOException | InvalidConfigurationException e) {
            LOGGER.error("An error occurred while loading '{}' config", resourceName, e);
        }

        load(yamlConfiguration, configClass);
    }

    public void load(@NotNull FileConfiguration fileConfiguration, @NotNull Class<?> configClass) {
        loadClass(fileConfiguration, configClass);
        for (Class<?> nestClazz : configClass.getNestMembers()) {
            loadClass(fileConfiguration, nestClazz);
        }
    }

    private void loadClass(@NotNull FileConfiguration fileConfiguration, @NotNull Class<?> configClazz) {
        for (Field field : configClazz.getFields()) {
            String path = null;
            try {
                if (!field.canAccess(null)) {
                    LOGGER.debugIfEnabled("Ignoring field '{}'", field.getName());
                    continue;
                }

                path = resolveConfigPath(configClazz, field);

                Class<?> fieldClazz = field.getType();

                Object defaultValue = field.get(null);
                Object value = get(fieldClazz, fileConfiguration, path, defaultValue);

                if (value != null && !value.equals(defaultValue)) {
                    field.set(null, value);
                }

            } catch (Exception e) {
                LOGGER.error("An error occurred while loading value from config, path: {}", path, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T get(@NotNull Class<?> clazz, @NotNull FileConfiguration fileConfiguration,
                      @NotNull String path, @Nullable Object defaultValue) {
        GetValueFunction<T> function = (GetValueFunction<T>) type2function.get(clazz);
        if (function == null) {
            throw new NullPointerException("Unsupported value type: " + clazz.getName());
        }
        return function.apply(fileConfiguration, path, (T) defaultValue);
    }

    @NotNull
    private static String resolveConfigPath(@NotNull Class<?> configClazz, @NotNull Field field) {

        String configClazzName = configClazz.getName();

        int dollarIndex = configClazzName.indexOf('$');
        if (dollarIndex != -1) {
            configClazzName = configClazzName.substring(configClazzName.indexOf('$')).replace('$', '.');
        } else {
            return field.getName().toLowerCase();
        }

        StringBuilder builder = new StringBuilder();

        char[] chars = configClazzName.toCharArray();
        for (int i = 1; i < chars.length; i++) {
            char prev = chars[i - 1];
            char current = chars[i];
            if (prev != '.' && Character.isUpperCase(current)) {
                builder.append('_');
            }
            builder.append(Character.toLowerCase(current));
        }

        return builder.append('.').append(field.getName().toLowerCase()).toString();
    }

    public interface GetValueFunction<T> {
        @Nullable
        T apply(@NotNull FileConfiguration config, @NotNull String path, @Nullable T def);
    }

}
