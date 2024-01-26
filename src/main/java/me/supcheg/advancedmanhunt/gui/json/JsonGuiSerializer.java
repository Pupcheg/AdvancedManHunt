package me.supcheg.advancedmanhunt.gui.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.ClickActions;
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
import me.supcheg.advancedmanhunt.gui.api.tick.AbstractTicker;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import me.supcheg.advancedmanhunt.json.JsonUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static me.supcheg.advancedmanhunt.json.Types.type;
import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public class JsonGuiSerializer implements TypeAdapterFactory {
    private final ReflectiveFunctionalLoader functionalLoader;
    private final AdvancedGuiController controller;
    private volatile Gson gson;
    private final Map<Type, TypeAdapter<?>> adapters = new HashMap<>();

    public JsonGuiSerializer(@NotNull Object obj, @NotNull AdvancedGuiController controller) {
        this.functionalLoader = new ReflectiveFunctionalLoader(obj);
        this.controller = controller;
        register(
                AdvancedGui.class,
                new AdvancedGuiAdapter()
        );
        register(
                GuiBackgroundFunction.class,
                new FunctionalAdapter<>(
                        Map.of(
                                ReflectiveFunctionalAdapterType.NAME, new ReflectiveFunctionalAdapterType<>(GuiBackgroundFunction::delegating),
                                "path", in -> GuiBackgroundFunction.constant(readString(in, "path"))
                        )
                )
        );
        register(
                GuiTicker.class,
                new TickerAdapter<>(GuiTickConsumer.class, GuiTicker::new)
        );
        register(
                GuiTickConsumer.class,
                new FunctionalAdapter<>(
                        Map.of(
                                ReflectiveFunctionalAdapterType.NAME, new ReflectiveFunctionalAdapterType<>(GuiTickConsumer::delegating)
                        )
                )
        );

        register(
                AdvancedButtonBuilder.class,
                new AdvancedButtonBuilderAdapter()
        );
        register(
                ButtonClickAction.class,
                new ButtonClickActionAdapter()
        );
        register(
                ButtonClickActionConsumer.class,
                new FunctionalAdapter<>(
                        Map.of(
                                ReflectiveFunctionalAdapterType.NAME, new ReflectiveFunctionalAdapterType<>(ButtonClickActionConsumer::delegating),
                                "perform_command", in -> ClickActions.performCommand(readString(in, "label")),
                                "open", in -> ClickActions.openGui(readString(in, "key"))
                        )
                )
        );
        register(
                ButtonTextureFunction.class,
                new FunctionalAdapter<>(
                        Map.of(
                                ReflectiveFunctionalAdapterType.NAME, new ReflectiveFunctionalAdapterType<>(ButtonTextureFunction::delegating),
                                "path", in -> ButtonTextureFunction.constant(readString(in, "path"))
                        )
                )
        );
        register(
                ButtonNameFunction.class,
                new FunctionalAdapter<>(
                        Map.of(
                                ReflectiveFunctionalAdapterType.NAME, new ReflectiveFunctionalAdapterType<>(ButtonNameFunction::delegating),
                                MiniMessageButtonNameFunctionalAdapterType.NAME, new MiniMessageButtonNameFunctionalAdapterType(),
                                JsonButtonNameFunctionalAdapterType.NAME, new JsonButtonNameFunctionalAdapterType()
                        )
                )
        );
        register(ButtonLoreFunction.class,
                new FunctionalAdapter<>(
                        Map.of(
                                ReflectiveFunctionalAdapterType.NAME, new ReflectiveFunctionalAdapterType<>(ButtonLoreFunction::delegating),
                                MiniMessageButtonLoreFunctionalAdapterType.NAME, new MiniMessageButtonLoreFunctionalAdapterType(),
                                JsonButtonLoreFunctionalAdapterType.NAME, new JsonButtonLoreFunctionalAdapterType()
                        )
                )
        );
        register(ButtonTicker.class,
                new TickerAdapter<>(ButtonTickConsumer.class, ButtonTicker::new)
        );
        register(ButtonTickConsumer.class,
                new FunctionalAdapter<>(
                        Map.of(
                                ReflectiveFunctionalAdapterType.NAME, new ReflectiveFunctionalAdapterType<>(ButtonTickConsumer::delegating)
                        )
                )
        );

        register(
                IntStream.class,
                new AdvancedIntAdapter()
        );
        register(
                Priority.class,
                new PriorityAdapter()
        );
        register(
                At.class,
                new AtAdapter()
        );
    }

    private <T> void register(@NotNull Class<T> clazz, @NotNull TypeAdapter<T> adapter) {
        adapters.put(clazz, adapter.nullSafe());
    }

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        if (this.gson == null) {
            synchronized (this) {
                if (this.gson == null) {
                    this.gson = gson;
                }
            }
        }

        TypeAdapter<?> adapter = adapters.get(type.getType());
        return adapter == null ? null : uncheckedCast(adapter.nullSafe());
    }


    private abstract static class ReadOnlyTypeAdapter<T> extends TypeAdapter<T> {
        @Override
        public void write(@NotNull JsonWriter out, @NotNull T value) {
            throw new UnsupportedOperationException("read-only");
        }
    }

    private class AdvancedGuiAdapter extends ReadOnlyTypeAdapter<AdvancedGui> {
        private static final String KEY = "key";
        private static final String ROWS = "rows";
        private static final String BACKGROUND = "background";
        private static final String BUTTONS = "buttons";
        private static final String TICKERS = "tickers";

        @NotNull
        @Override
        public AdvancedGui read(@NotNull JsonReader in) throws IOException {
            String key = null;
            Integer rows = null;
            GuiBackgroundFunction background = null;
            List<AdvancedButtonBuilder> buttons = Collections.emptyList();
            List<GuiTicker> tickers = Collections.emptyList();

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case KEY -> key = in.nextString();
                    case ROWS -> rows = in.nextInt();
                    case BACKGROUND -> background = gson.fromJson(in, GuiBackgroundFunction.class);
                    case BUTTONS -> buttons = gson.fromJson(in, type(List.class, AdvancedButtonBuilder.class));
                    case TICKERS -> tickers = gson.fromJson(in, type(List.class, GuiTicker.class));
                    default -> throw unknownNameException(name, in);
                }
            }
            in.endObject();

            assertNonNull(key, KEY, in);
            assertNonNull(rows, ROWS, in);
            assertNonNull(buttons, BUTTONS, in);
            assertNonNull(tickers, TICKERS, in);

            AdvancedGuiBuilder builder = controller.gui();
            builder.key(key);
            builder.rows(rows);
            apply(builder::background, background);
            buttons.forEach(builder::button);
            tickers.forEach(builder::ticker);

            return builder.buildAndRegister();
        }
    }

    @RequiredArgsConstructor
    private class TickerAdapter<T extends AbstractTicker<T, C>, C extends Consumer<?>> extends ReadOnlyTypeAdapter<T> {
        private static final String AT = "at";
        private static final String PRIORITY = "priority";
        private static final String CONSUMER = "consumer";

        private final Class<C> consumerClass;
        private final TickerBuilder<T, C> tickerBuilder;

        @NotNull
        @Override
        public T read(@NotNull JsonReader in) throws IOException {
            At at = null;
            Priority priority = Priority.NORMAL;
            C consumer = null;

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case AT -> at = gson.fromJson(in, At.class);
                    case PRIORITY -> priority = gson.fromJson(in, Priority.class);
                    case CONSUMER -> consumer = gson.fromJson(in, consumerClass);
                    default -> throw unknownNameException(name, in);
                }
            }
            in.endObject();

            assertNonNull(at, AT, in);
            assertNonNull(priority, PRIORITY, in);
            assertNonNull(consumer, CONSUMER, in);

            return tickerBuilder.build(at, priority, consumer);
        }
    }

    private interface TickerBuilder<T extends AbstractTicker<T, C>, C extends Consumer<?>> {
        @NotNull
        T build(@NotNull At at, @NotNull Priority priority, @NotNull C consumer);
    }

    private static class PriorityAdapter extends ReadOnlyTypeAdapter<Priority> {
        private static final String PRIORITY = "priority";

        @NotNull
        @Override
        public Priority read(@NotNull JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NUMBER) {
                return Priority.fromValue(in.nextInt());
            }

            String raw = in.nextString();
            Priority priority = Priority.fromName(raw);
            assertNonNull(priority, PRIORITY, in);
            return priority;
        }
    }

    private static class AtAdapter extends ReadOnlyTypeAdapter<At> {
        private static final String AT = "at";

        @NotNull
        @Override
        public At read(@NotNull JsonReader in) throws IOException {
            String raw = in.nextString();
            At at = At.valueOf(raw.toUpperCase());
            assertNonNull(at, AT, in);
            return at;
        }
    }

    private class AdvancedButtonBuilderAdapter extends ReadOnlyTypeAdapter<AdvancedButtonBuilder> {
        private static final String SLOTS = "slots";
        private static final String SLOT = "slot";
        private static final String DEFAULT_ENABLED = "default_enabled";
        private static final String DEFAULT_SHOWN = "default_shown";
        private static final String CLICK_ACTIONS = "click_actions";
        private static final String TEXTURE = "texture";
        private static final String NAME = "name";
        private static final String LORE = "lore";
        private static final String TICKERS = "tickers";
        private static final String DEFAULT_ENCHANTED = "default_enchanted";

        @NotNull
        @Override
        public AdvancedButtonBuilder read(@NotNull JsonReader in) throws IOException {
            IntStream slots = null;
            Boolean defaultEnabled = null;
            Boolean defaultShown = null;
            List<ButtonClickAction> clickActions = Collections.emptyList();
            ButtonTextureFunction texture = null;
            ButtonNameFunction nameFunction = null;
            ButtonLoreFunction lore = null;
            List<ButtonTicker> tickers = Collections.emptyList();
            Boolean defaultEnchanted = null;

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case SLOTS, SLOT -> slots = gson.fromJson(in, IntStream.class);
                    case DEFAULT_ENABLED -> defaultEnabled = in.nextBoolean();
                    case DEFAULT_SHOWN -> defaultShown = in.nextBoolean();
                    case CLICK_ACTIONS -> clickActions = gson.fromJson(in, type(List.class, ButtonClickAction.class));
                    case TEXTURE -> texture = gson.fromJson(in, ButtonTextureFunction.class);
                    case NAME -> nameFunction = gson.fromJson(in, ButtonNameFunction.class);
                    case LORE -> lore = gson.fromJson(in, ButtonLoreFunction.class);
                    case TICKERS -> tickers = gson.fromJson(in, type(List.class, ButtonTicker.class));
                    case DEFAULT_ENCHANTED -> defaultEnchanted = in.nextBoolean();
                    default -> throw unknownNameException(name, in);
                }
            }
            in.endObject();

            assertNonNull(slots, SLOTS, in);
            assertNonNull(clickActions, CLICK_ACTIONS, in);
            assertNonNull(texture, TEXTURE, in);

            AdvancedButtonBuilder builder = controller.button();
            builder.slot(slots);
            apply(builder::defaultEnabled, defaultEnabled);
            apply(builder::defaultShown, defaultShown);
            clickActions.forEach(builder::clickAction);
            apply(builder::name, nameFunction);
            apply(builder::lore, lore);
            tickers.forEach(builder::ticker);
            apply(builder::defaultEnchanted, defaultEnchanted);

            return builder;
        }
    }

    private class ButtonClickActionAdapter extends ReadOnlyTypeAdapter<ButtonClickAction> {
        private static final String PRIORITY = "priority";
        private static final String CONSUMER = "consumer";

        @NotNull
        @Override
        public ButtonClickAction read(@NotNull JsonReader in) throws IOException {
            Priority priority = Priority.NORMAL;
            ButtonClickActionConsumer consumer = null;

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case PRIORITY -> priority = gson.fromJson(in, Priority.class);
                    case CONSUMER -> consumer = gson.fromJson(in, ButtonClickActionConsumer.class);
                    default -> throw unknownNameException(name, in);
                }
            }
            in.endObject();

            assertNonNull(priority, PRIORITY, in);
            assertNonNull(consumer, CONSUMER, in);

            return new ButtonClickAction(priority, consumer);
        }

    }

    @RequiredArgsConstructor
    private static class FunctionalAdapter<I> extends ReadOnlyTypeAdapter<I> {
        private static final String TYPE = "type";

        private final Map<String, FunctionalAdapterType<I>> adapters;

        @NotNull
        @Override
        public I read(@NotNull JsonReader in) throws IOException {
            String type = null;

            in.beginObject();
            if (in.nextName().equals(TYPE)) {
                type = in.nextString();
            }
            assertNonNull(type, TYPE, in);

            FunctionalAdapterType<I> adapter = adapters.get(type);
            if (adapter == null) {
                throw new IllegalStateException(
                        "Type '%s' is not supported".formatted(type)
                );
            }
            I functionalInterface = adapter.create(in);

            in.endObject();

            return functionalInterface;
        }
    }

    @NotNull
    private String readString(@NotNull JsonReader in, @NotNull String name) throws IOException {
        String value = null;
        if (in.nextName().equals(name)) {
            value = in.nextString();
        }
        assertNonNull(value, name, in);
        return value;
    }

    private interface FunctionalAdapterType<I> {
        @NotNull
        I create(@NotNull JsonReader in) throws IOException;
    }

    @RequiredArgsConstructor
    private class ReflectiveFunctionalAdapterType<I> implements FunctionalAdapterType<I> {
        private static final String NAME = "reflect";
        private static final String METHOD = "method";
        private final Function<MethodHandle, I> interfaceConstructor;

        @NotNull
        @Override
        public I create(@NotNull JsonReader in) throws IOException {
            return functionalLoader.create(readString(in, METHOD), interfaceConstructor);
        }
    }

    private class MiniMessageButtonNameFunctionalAdapterType implements FunctionalAdapterType<ButtonNameFunction> {
        private static final String NAME = "minimessage";
        private static final String TEXT = "text";

        @NotNull
        @Override
        public ButtonNameFunction create(@NotNull JsonReader in) throws IOException {
            return ButtonNameFunction.constant(miniMessage().deserialize(readString(in, TEXT)).compact());
        }
    }

    private static class JsonButtonNameFunctionalAdapterType implements FunctionalAdapterType<ButtonNameFunction> {
        private static final String NAME = "json";
        private static final String JSON = "json";

        @NotNull
        @Override
        public ButtonNameFunction create(@NotNull JsonReader in) throws IOException {
            Component text = null;

            if (in.hasNext() && in.nextName().equals(JSON)) {
                text = gson().serializer().fromJson(in, type(List.class, Component.class));
            }
            assertNonNull(text, JSON, in);

            return ButtonNameFunction.constant(text.compact());
        }
    }

    private class MiniMessageButtonLoreFunctionalAdapterType implements FunctionalAdapterType<ButtonLoreFunction> {
        private static final String NAME = "minimessage";
        private static final String TEXT = "text";

        @NotNull
        @Override
        public ButtonLoreFunction create(@NotNull JsonReader in) throws IOException {
            List<String> text = Collections.emptyList();

            if (in.hasNext() && in.nextName().equals(TEXT)) {
                text = gson.fromJson(in, type(List.class, String.class));
            }
            assertNonNull(text, TEXT, in);

            List<Component> deserialized = text.stream()
                    .map(miniMessage()::deserialize)
                    .map(Component::compact)
                    .toList();
            return ButtonLoreFunction.constant(deserialized);
        }
    }

    private static class JsonButtonLoreFunctionalAdapterType implements FunctionalAdapterType<ButtonLoreFunction> {
        private static final String NAME = "json";
        private static final String JSON = "json";

        @NotNull
        @Override
        public ButtonLoreFunction create(@NotNull JsonReader in) throws IOException {
            List<Component> text = Collections.emptyList();

            if (in.hasNext() && in.nextName().equals(JSON)) {
                text = gson().serializer().fromJson(in, type(List.class, Component.class));
            }
            assertNonNull(text, JSON, in);

            List<Component> deserialized = text.stream()
                    .map(Component::compact)
                    .toList();
            return ButtonLoreFunction.constant(deserialized);
        }
    }

    @NotNull
    private static BadPropertyException unknownNameException(@NotNull String name, @NotNull JsonReader reader) {
        return new BadPropertyException("Unknown property name: '" + name + "'" + JsonUtil.getLocationString(reader));
    }

    @Contract(value = "null, _, _ -> fail; !null, _, _ -> _", pure = true)
    private static void assertNonNull(@Nullable Object o, @NotNull String name, @NotNull JsonReader reader)
            throws BadPropertyException {
        Objects.requireNonNull(name, "name");
        if (o == null) {
            throw new BadPropertyException(
                    "Property '" + name + "' is not set or configured wrongly" +
                            JsonUtil.getLocationString(reader)
            );
        }
    }

    private static <A> void apply(@NotNull Consumer<A> consumer, @Nullable A arg) {
        Objects.requireNonNull(consumer, "consumer");
        if (arg != null) {
            consumer.accept(arg);
        }
    }
}
