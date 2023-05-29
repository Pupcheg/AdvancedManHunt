package me.supcheg.advancedmanhunt.lang;

import com.google.common.io.MoreFiles;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class LanguageLoader {
    private final AdvancedManHuntPlugin plugin;
    private final CustomLogger logger;

    public LanguageLoader(@NotNull AdvancedManHuntPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getSLF4JLogger().newChild(LanguageLoader.class);
    }

    public void setup() {
        TranslationRegistry registry = TranslationRegistry.create(Key.key(AdvancedManHuntPlugin.PLUGIN_NAME, "default"));
        Type mapType = Types.type(Map.class, String.class, String.class);

        try (FileSystem fileSystem = FileSystems.newFileSystem(plugin.getJarPath())) {

            Set<Path> langPaths;
            try (Stream<Path> languages = Files.list(fileSystem.getPath("lang"))) {
                langPaths = languages.collect(Collectors.toSet());
            }
            for (Path langPath : langPaths) {
                String fileName = MoreFiles.getNameWithoutExtension(langPath);

                Locale locale = Translator.parseLocale(fileName);
                Objects.requireNonNull(locale, "Unable to parse locale: " + fileName);

                Map<String, String> key2translate;

                try (BufferedReader reader = Files.newBufferedReader(langPath)) {
                    key2translate = plugin.getGson().fromJson(reader, mapType);
                }

                key2translate.forEach((key, translate) -> registry.register(key, locale, new MessageFormat(translate)));
                logger.debugIfEnabled("Loaded {} localization entries for {}", key2translate.size(), locale.toString());

            }

        } catch (IOException e) {
            logger.error("An error occurred while loading languages", e);
        }

        GlobalTranslator.translator().addSource(registry);
    }
}
