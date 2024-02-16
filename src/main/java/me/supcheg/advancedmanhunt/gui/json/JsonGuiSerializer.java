package me.supcheg.advancedmanhunt.gui.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
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
import me.supcheg.advancedmanhunt.util.reflect.InstantMethodHandleLookup;
import me.supcheg.advancedmanhunt.util.reflect.MethodHandleLookup;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class JsonGuiSerializer implements TypeAdapterFactory {
    private final Map<Type, Function<Gson, TypeAdapter<?>>> adapters;

    public JsonGuiSerializer() {
        this.adapters = Maps.newHashMapWithExpectedSize(17);
        MethodHandleLookup lookup = new InstantMethodHandleLookup();
        register(
                AdvancedGuiBuilder.class,
                AdvancedGuiBuilderAdapter::new
        );
        register(
                GuiTicker.class,
                gson -> new TickerAdapter<>(GuiTickConsumer.class, GuiTicker::new, gson)
        );
        register(
                GuiTickConsumer.class,
                new FunctionalAdapter<>(
                        new MethodDelegatingType<>(MethodGuiTickConsumer::new, GuiTickContext.class, lookup)
                )
        );

        register(
                AdvancedButtonBuilder.class,
                AdvancedButtonBuilderAdapter::new
        );
        register(
                ButtonClickAction.class,
                ButtonClickActionAdapter::new
        );
        register(
                ButtonClickActionConsumer.class,
                gson -> new FunctionalAdapter<>(
                        new MethodDelegatingType<>(MethodButtonClickActionConsumer::new, ButtonClickContext.class, lookup),
                        PerformCommandButtonClickActionConsumerType.INSTANCE,
                        OpenGuiButtonClickActionConsumerType.INSTANCE,
                        new PlaySoundButtonClickActionConsumerType(gson)
                )
        );
        register(
                ButtonTicker.class,
                gson -> new TickerAdapter<>(ButtonTickConsumer.class, ButtonTicker::new, gson)
        );
        register(
                ButtonTickConsumer.class,
                new FunctionalAdapter<>(
                        new MethodDelegatingType<>(MethodButtonTickConsumer::new, ButtonTickContext.class, lookup)
                )
        );
        register(
                DefaultButtonConfigurer.class,
                DefaultButtonConfigurerAdapter::new
        );

        register(IntStream.class, AdvancedIntAdapter.INSTANCE);
        register(Priority.class, PriorityAdapter.INSTANCE);
        register(At.class, AtAdapter.INSTANCE);
        register(Component.class, MiniMessageComponentAdapter.INSTANCE);
        register(Sound.class, AdvancedSoundAdapter::new);
        register(Key.class, KeyAdapter.INSTANCE);
        register(Sound.Source.class, SoundSourceAdapter.INSTANCE);
    }

    private <T> void register(@NotNull Class<T> clazz, @NotNull TypeAdapter<T> adapter) {
        adapters.put(clazz, __ -> adapter);
    }

    private <T> void register(@NotNull Class<T> clazz, @NotNull Function<Gson, TypeAdapter<T>> adapter) {
        adapters.put(clazz, uncheckedCast(adapter));
    }

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        Function<Gson, TypeAdapter<?>> adapter = adapters.get(type.getType());
        return adapter == null ? null : uncheckedCast(adapter.apply(gson).nullSafe());
    }
}
