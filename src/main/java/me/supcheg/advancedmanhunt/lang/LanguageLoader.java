package me.supcheg.advancedmanhunt.lang;

import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LanguageLoader {
    private final AdvancedManHuntPlugin plugin;
    private final CustomLogger logger;

    public LanguageLoader(@NotNull AdvancedManHuntPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getSLF4JLogger().newChild(LanguageLoader.class);
    }

    public void setup() {
        TranslationRegistry registry = TranslationRegistry.create(Key.key(AdvancedManHuntPlugin.PLUGIN_NAME, "default"));
        Type mapType = Types.type(Map.class, String.class, MessageFormat.class);

        // TODO: 09.06.2023 automatic language searching
        Set<String> langKeys = Set.of("ru_RU");
        for (String langKey : langKeys) {

            Locale locale = Translator.parseLocale(langKey);
            Objects.requireNonNull(locale, "Unable to parse locale: " + langKey);

            Map<String, MessageFormat> key2translate;

            try (BufferedReader reader = plugin.getContainerAdapter().readResource("lang/" + langKey + ".json")) {
                key2translate = plugin.getGson().fromJson(reader, mapType);
            } catch (Exception e) {
                logger.error("An error occurred while loading languages", e);
                continue;
            }

            registry.registerAll(locale, key2translate);
            logger.debugIfEnabled("Loaded {} localization entries for {}", key2translate.size(), locale.toString());
        }

        GlobalTranslator.translator().addSource(registry);
    }
}
