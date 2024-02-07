package me.supcheg.advancedmanhunt.gui.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapter;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodButtonTickConsumer;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodGuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodGuiTickConsumer;
import me.supcheg.advancedmanhunt.gui.json.functional.type.JsonButtonLoreFunctionType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.JsonButtonNameFunctionType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.MiniMessageButtonLoreType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.MiniMessageButtonNameFunctionType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.OpenGuiButtonClickActionConsumerType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.PathButtonTextureFunctionType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.PathGuiBackgroundFunctionType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.PerformCommandButtonClickActionConsumerType;
import me.supcheg.advancedmanhunt.gui.json.functional.type.MethodDelegatingType;
import me.supcheg.advancedmanhunt.gui.json.layer.AdvancedButtonBuilderAdapter;
import me.supcheg.advancedmanhunt.gui.json.layer.AdvancedGuiAdapter;
import me.supcheg.advancedmanhunt.gui.json.layer.AdvancedGuiBuilderAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.AdvancedIntAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.AtAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.ButtonClickActionAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.PriorityAdapter;
import me.supcheg.advancedmanhunt.gui.json.misc.TickerAdapter;
import me.supcheg.advancedmanhunt.util.Unchecked;
import me.supcheg.advancedmanhunt.util.reflect.MethodHandleLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class JsonGuiSerializer implements TypeAdapterFactory {
    private final Map<Type, BiFunction<Gson, AdvancedGuiController, TypeAdapter<?>>> adapters;
    private final AdvancedGuiController controller;

    public JsonGuiSerializer(@NotNull AdvancedGuiController controller, @NotNull MethodHandleLookup lookup) {
        this.adapters = Maps.newHashMapWithExpectedSize(16);
        this.controller = controller;
        register(
                AdvancedGui.class,
                AdvancedGuiAdapter::new
        );
        register(
                AdvancedGuiBuilder.class,
                AdvancedGuiBuilderAdapter::new
        );
        register(
                GuiBackgroundFunction.class,
                new FunctionalAdapter<>(
                        Map.of(
                                MethodDelegatingType.NAME, new MethodDelegatingType<>(MethodGuiBackgroundFunction::new, lookup),
                                PathGuiBackgroundFunctionType.NAME, new PathGuiBackgroundFunctionType()
                        )
                )
        );
        register(
                GuiTicker.class,
                gson -> new TickerAdapter<>(GuiTickConsumer.class, GuiTicker::new, gson)
        );
        register(
                GuiTickConsumer.class,
                new FunctionalAdapter<>(
                        Map.of(
                                MethodDelegatingType.NAME, new MethodDelegatingType<>(MethodGuiTickConsumer::new, lookup)
                        )
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
                new FunctionalAdapter<>(
                        Map.of(
                                MethodDelegatingType.NAME, new MethodDelegatingType<>(MethodButtonClickActionConsumer::new, lookup),
                                PerformCommandButtonClickActionConsumerType.NAME, new PerformCommandButtonClickActionConsumerType(),
                                OpenGuiButtonClickActionConsumerType.NAME, new OpenGuiButtonClickActionConsumerType()
                        )
                )
        );
        register(
                ButtonTextureFunction.class,
                new FunctionalAdapter<>(
                        Map.of(
                                MethodDelegatingType.NAME, new MethodDelegatingType<>(MethodButtonTextureFunction::new, lookup),
                                PathButtonTextureFunctionType.NAME, new PathButtonTextureFunctionType()
                        )
                )
        );
        register(
                ButtonNameFunction.class,
                new FunctionalAdapter<>(
                        Map.of(
                                MethodDelegatingType.NAME, new MethodDelegatingType<>(MethodButtonNameFunction::new, lookup),
                                MiniMessageButtonNameFunctionType.NAME, new MiniMessageButtonNameFunctionType(),
                                JsonButtonNameFunctionType.NAME, new JsonButtonNameFunctionType()
                        )
                )
        );
        register(ButtonLoreFunction.class,
                gson -> new FunctionalAdapter<>(
                        Map.of(
                                MethodDelegatingType.NAME, new MethodDelegatingType<>(MethodButtonLoreFunction::new, lookup),
                                MiniMessageButtonLoreType.NAME, new MiniMessageButtonLoreType(gson),
                                JsonButtonLoreFunctionType.NAME, new JsonButtonLoreFunctionType()
                        )
                )
        );
        register(ButtonTicker.class,
                gson -> new TickerAdapter<>(ButtonTickConsumer.class, ButtonTicker::new, gson)
        );
        register(ButtonTickConsumer.class,
                new FunctionalAdapter<>(
                        Map.of(
                                MethodDelegatingType.NAME, new MethodDelegatingType<>(MethodButtonTickConsumer::new, lookup)
                        )
                )
        );

        register(IntStream.class, AdvancedIntAdapter.INSTANCE);
        register(Priority.class, PriorityAdapter.INSTANCE);
        register(At.class, AtAdapter.INSTANCE);
    }

    private <T> void register(@NotNull Class<T> clazz, @NotNull TypeAdapter<T> adapter) {
        adapters.put(clazz, (__, ___) -> adapter);
    }

    private <T> void register(@NotNull Class<T> clazz, @NotNull Function<Gson, TypeAdapter<T>> adapter) {
        adapters.put(clazz, (gson, __) -> adapter.apply(gson));
    }

    private <T> void register(@NotNull Class<T> clazz, @NotNull BiFunction<Gson, AdvancedGuiController, TypeAdapter<T>> adapter) {
        adapters.put(clazz, adapter::apply);
    }

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        BiFunction<Gson, AdvancedGuiController, TypeAdapter<?>> adapter = adapters.get(type.getType());
        return adapter == null ? null : Unchecked.uncheckedCast(adapter.apply(gson, controller).nullSafe());
    }
}
