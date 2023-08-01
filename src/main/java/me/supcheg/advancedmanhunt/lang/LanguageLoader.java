package me.supcheg.advancedmanhunt.lang;

import com.google.common.io.MoreFiles;
import com.google.gson.Gson;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@CustomLog
@RequiredArgsConstructor
public class LanguageLoader {
    private final Type string2messageFormatMapType = Types.type(Map.class, String.class, MessageFormat.class);
    private final ContainerAdapter containerAdapter;
    private final Gson gson;

    private TranslationRegistry translationRegistry;

    public void load() {
        createTranslationRegistry();
        loadAllFromResources();
        appendGlobalTranslator();
    }

    @SneakyThrows
    private void loadAllFromResources() {
        try (Stream<Path> lang = Files.walk(containerAdapter.resolveResource("lang"))) {
            lang.filter(Files::isRegularFile).forEach(this::loadLanguage);
        }
    }

    private void createTranslationRegistry() {
        translationRegistry = TranslationRegistry.create(Key.key(AdvancedManHuntPlugin.NAMESPACE, "default"));
    }

    private void appendGlobalTranslator() {
        GlobalTranslator.translator().addSource(translationRegistry);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void loadLanguage(@NotNull Path path) {
        String langKey = MoreFiles.getNameWithoutExtension(path);

        Locale locale = Translator.parseLocale(langKey);
        Objects.requireNonNull(locale, "Unable to parse locale: " + langKey);

        Map<String, MessageFormat> key2translate;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            key2translate = gson.fromJson(reader, string2messageFormatMapType);
        } catch (Exception e) {
            log.error("An error occurred while loading languages", e);
            return;
        }

        translationRegistry.registerAll(locale, key2translate);
        log.debugIfEnabled("Loaded {} localization entries for {}", key2translate.size(), locale.toString());
    }
}
