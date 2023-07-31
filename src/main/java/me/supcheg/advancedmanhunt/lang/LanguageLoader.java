package me.supcheg.advancedmanhunt.lang;

import com.google.common.io.MoreFiles;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;

import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@AllArgsConstructor
public class LanguageLoader {
    private static final CustomLogger LOGGER = CustomLogger.getLogger(LanguageLoader.class);

    private final ContainerAdapter containerAdapter;
    private final Gson gson;

    @SuppressWarnings("UnstableApiUsage")
    public void loadAllFromResources() {
        TranslationRegistry registry = TranslationRegistry.create(Key.key(AdvancedManHuntPlugin.NAMESPACE, "default"));
        Type mapType = Types.type(Map.class, String.class, MessageFormat.class);

        Collection<String> langKeys;
        try (Stream<Path> lang = containerAdapter.readResourcesTree("lang")) {
            langKeys = lang.map(MoreFiles::getNameWithoutExtension).toList();
        }

        for (String langKey : langKeys) {

            Locale locale = Translator.parseLocale(langKey);
            Objects.requireNonNull(locale, "Unable to parse locale: " + langKey);

            Map<String, MessageFormat> key2translate;

            try (BufferedReader reader = containerAdapter.readResource("lang/" + langKey + ".json")) {
                key2translate = gson.fromJson(reader, mapType);
            } catch (Exception e) {
                LOGGER.error("An error occurred while loading languages", e);
                continue;
            }

            registry.registerAll(locale, key2translate);
            LOGGER.debugIfEnabled("Loaded {} localization entries for {}", key2translate.size(), locale.toString());
        }

        GlobalTranslator.translator().addSource(registry);
    }
}
