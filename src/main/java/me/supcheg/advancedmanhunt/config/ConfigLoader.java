package me.supcheg.advancedmanhunt.config;

import com.google.common.collect.Maps;
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
import lombok.CustomLog;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.util.ComponentUtil;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.LocationParser;
import me.supcheg.advancedmanhunt.util.Unchecked;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

@CustomLog
@SuppressWarnings("PatternValidation")
public class ConfigLoader {
    private final ContainerAdapter containerAdapter;
    private final Map<Class<?>, GetValueFunction<?>> type2getFunction;

    @SneakyThrows
    public ConfigLoader(@NotNull ContainerAdapter containerAdapter) {
        this.containerAdapter = containerAdapter;
        this.type2getFunction = Maps.newLinkedHashMapWithExpectedSize(19);

        register(String.class, (config, path) -> {
            List<String> list = config.getStringList(path);
            return !list.isEmpty() ? String.join("\n", list) : config.getString(path);
        });

        register(Component.class, (config, path) -> ComponentUtil.deserializeWithNoItalic(config.getString(path)));

        register(Sound.class, (config, path) -> {
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section == null) {
                String rawKey = config.getString(path);
                if (rawKey == null) {
                    return null;
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
        register(Duration.class, (config, path) -> {
            String serialized = config.getString(path);
            if (serialized == null) {
                return null;
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

        register(ImmutableLocation.class, (config, path) -> {
            String serialized = config.getString(path);
            if (serialized == null) {
                return null;
            }
            return LocationParser.parseImmutableLocation(serialized);
        });

        Pattern distancePattern = Pattern.compile("\\d+[bcr]", Pattern.CASE_INSENSITIVE);
        register(Distance.class, (config, path) -> {
            String serialized = config.getString(path);
            if (serialized == null) {
                return null;
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

        Pattern intLimitPattern = Pattern.compile("\\d+-\\d+");
        register(IntLimit.class, (config, path) -> {
            String serialized = config.getString(path);
            if (serialized == null) {
                return null;
            }
            if (!intLimitPattern.matcher(serialized).matches()) {
                throw new IllegalArgumentException("'%s' is not a valid int limit".formatted(serialized));
            }

            int separatorIndex = serialized.indexOf('-');

            int minValue = Integer.parseInt(serialized.substring(0, separatorIndex));
            int maxValue = Integer.parseInt(serialized.substring(separatorIndex + 1));

            return IntLimit.of(minValue, maxValue);
        });

        register(byte.class, number(Number::byteValue));
        register(short.class, number(Number::shortValue));
        register(int.class, number(Number::intValue));
        register(long.class, number(Number::longValue));
        register(float.class, number(Number::floatValue));
        register(double.class, number(Number::doubleValue));
        register(boolean.class, (config, path) -> (Boolean) config.get(path));

        register(List.class, list(FileConfiguration::getList, UnaryOperator.identity(), Collections::unmodifiableList));
        register(IntList.class, list(FileConfiguration::getIntegerList, IntArrayList::new, IntLists::unmodifiable));
        register(LongList.class, list(FileConfiguration::getLongList, LongArrayList::new, LongLists::unmodifiable));
        register(DoubleList.class, list(FileConfiguration::getDoubleList, DoubleArrayList::new, DoubleLists::unmodifiable));
        register(BooleanList.class, list(FileConfiguration::getBooleanList, BooleanArrayList::new, BooleanLists::unmodifiable));
    }

    public <T> void register(@NotNull Class<T> clazz,
                             @NotNull GetValueFunction<? extends T> function) {
        type2getFunction.put(clazz, function);
    }

    @NotNull
    @Contract("_ -> new")
    public static <T> GetValueFunction<T> number(@NotNull Function<Number, T> function) {
        return (config, path) -> {
            Number number = (Number) config.get(path);
            return number == null ? null : function.apply(number);
        };
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static <P, B extends List<?>> GetValueFunction<P> list(@NotNull BiFunction<FileConfiguration, String, B> getList,
                                                                  @NotNull Function<B, P> toPrimitiveList,
                                                                  @NotNull UnaryOperator<P> toUnmodifiable) {
        return (config, path) -> {
            B boxed = getList.apply(config, path);
            if (boxed == null) {
                return null;
            }

            P primitive = toPrimitiveList.apply(boxed);
            Objects.requireNonNull(primitive, "primitive");

            P unmodifiable = toUnmodifiable.apply(primitive);
            Objects.requireNonNull(unmodifiable, "unmodifiable");
            return unmodifiable;
        };
    }

    public void load(@NotNull String resourceName, @NotNull Class<?> configClass) {
        log.debugIfEnabled("Loading {} class from {}", configClass.getSimpleName(), resourceName);
        Path path = containerAdapter.unpackResource(resourceName);

        YamlConfiguration yamlConfiguration = new YamlConfiguration();

        try (Reader reader = Files.newBufferedReader(path)) {
            yamlConfiguration.load(reader);
        } catch (IOException | InvalidConfigurationException e) {
            log.error("An error occurred while loading '{}' config", resourceName, e);
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
                    log.debugIfEnabled("Ignoring field '{}'", field.getName());
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
                log.error("An error occurred while loading value from config, path: {}, yaml: {}", path, fileConfiguration.saveToString(), e);
            }
        }
    }

    @Nullable
    @Contract("_, _, _, !null -> !null")
    private Object get(@NotNull Class<?> clazz, @NotNull FileConfiguration fileConfiguration,
                       @NotNull String path, @Nullable Object defaultValue) {
        GetValueFunction<Object> function = Unchecked.uncheckedCast(type2getFunction.get(clazz));
        if (function == null) {
            throw new NullPointerException("Unsupported value type: " + clazz.getName());
        }
        Object ret = function.getValue(fileConfiguration, path);
        return ret == null ? defaultValue : ret;
    }

    @NotNull
    private static String resolveConfigPath(@NotNull Class<?> configClazz, @NotNull Field field) {
        String configClazzName = configClazz.getName();

        int dollarIndex = configClazzName.indexOf('$');
        if (dollarIndex != -1) {
            configClazzName = configClazzName.substring(dollarIndex).replace('$', '.');
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
        T getValue(@NotNull FileConfiguration config, @NotNull String path);
    }

}
