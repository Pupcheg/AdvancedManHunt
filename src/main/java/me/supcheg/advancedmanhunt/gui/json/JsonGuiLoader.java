package me.supcheg.advancedmanhunt.gui.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiLoader;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import me.supcheg.advancedmanhunt.gui.api.context.GuiTickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import me.supcheg.advancedmanhunt.gui.json.functional.DefaultButtonConfigurer;
import me.supcheg.advancedmanhunt.gui.json.functional.DefaultButtonConfigurerAdapter;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapter;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodButtonTickConsumer;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodGuiTickConsumer;
import me.supcheg.advancedmanhunt.gui.json.functional.type.MethodDelegatingType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.OpenGuiButtonClickActionConsumerType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.PerformCommandButtonClickActionConsumerType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.PlaySoundButtonClickActionConsumerType;
import me.supcheg.advancedmanhunt.gui.json.layer.AdvancedButtonBuilderAdapter;
import me.supcheg.advancedmanhunt.gui.json.layer.AdvancedGuiBuilderAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.AdvancedIntAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.AdvancedSoundAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.AtAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.ButtonClickActionAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.KeyAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.MiniMessageComponentAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.PriorityAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.SoundSourceAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.TickerAdapter;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.reflect.InstantMethodHandleLookup;
import me.supcheg.advancedmanhunt.util.MapTypeAdapterFactory;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class JsonGuiLoader implements AdvancedGuiLoader {
    private final ContainerAdapter containerAdapter;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(
                    new MapTypeAdapterFactory()
                            .typeAdapter(AdvancedGuiBuilder.class, AdvancedGuiBuilderAdapter::new)
                            .typeAdapter(GuiTicker.class,
                                    gson -> new TickerAdapter<>(GuiTickConsumer.class, GuiTicker::new, gson)
                            )
                            .typeAdapter(GuiTickConsumer.class,
                                    () -> new FunctionalAdapter<>(
                                            new MethodDelegatingType<>(
                                                    MethodGuiTickConsumer::new,
                                                    GuiTickContext.class,
                                                    InstantMethodHandleLookup.INSTANCE
                                            )
                                    )
                            )
                            .typeAdapter(AdvancedButtonBuilder.class, AdvancedButtonBuilderAdapter::new)
                            .typeAdapter(ButtonClickAction.class, ButtonClickActionAdapter::new)
                            .typeAdapter(ButtonClickActionConsumer.class,
                                    gson -> new FunctionalAdapter<>(
                                            new MethodDelegatingType<>(
                                                    MethodButtonClickActionConsumer::new,
                                                    ButtonClickContext.class,
                                                    InstantMethodHandleLookup.INSTANCE
                                            ),
                                            new PerformCommandButtonClickActionConsumerType(),
                                            new OpenGuiButtonClickActionConsumerType(),
                                            new PlaySoundButtonClickActionConsumerType(gson)
                                    )
                            )
                            .typeAdapter(ButtonTicker.class,
                                    gson -> new TickerAdapter<>(ButtonTickConsumer.class, ButtonTicker::new, gson)
                            )
                            .typeAdapter(ButtonTickConsumer.class,
                                    () -> new FunctionalAdapter<>(
                                            new MethodDelegatingType<>(
                                                    MethodButtonTickConsumer::new,
                                                    ButtonTickContext.class,
                                                    InstantMethodHandleLookup.INSTANCE
                                            )
                                    )
                            )
                            .typeAdapter(DefaultButtonConfigurer.class, DefaultButtonConfigurerAdapter::new)
                            .typeAdapter(IntStream.class, AdvancedIntAdapter::new)
                            .typeAdapter(Priority.class, PriorityAdapter::new)
                            .typeAdapter(At.class, AtAdapter::new)
                            .typeAdapter(Component.class, MiniMessageComponentAdapter::new)
                            .typeAdapter(Sound.class, AdvancedSoundAdapter::new)
                            .typeAdapter(Key.class, KeyAdapter::new)
                            .typeAdapter(Sound.Source.class, SoundSourceAdapter::new)
            )
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @NotNull
    @Override
    public AdvancedGuiBuilder loadResource(@NotNull String path) throws IOException {
        try (BufferedReader in = Files.newBufferedReader(containerAdapter.resolveResource(path))) {
            return loadResource(in);
        }
    }

    @NotNull
    @Override
    public AdvancedGuiBuilder loadResource(@NotNull Reader in) {
        return gson.fromJson(in, AdvancedGuiBuilder.class);
    }

    @Override
    public void saveResource(@NotNull AdvancedGuiBuilder gui, @NotNull String path) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(containerAdapter.resolveData(path))) {
            saveResource(gui, out);
        }
    }

    @Override
    public void saveResource(@NotNull AdvancedGuiBuilder gui, @NotNull Writer out) {
        gson.toJson(gui, AdvancedGuiBuilder.class, out);
    }
}
