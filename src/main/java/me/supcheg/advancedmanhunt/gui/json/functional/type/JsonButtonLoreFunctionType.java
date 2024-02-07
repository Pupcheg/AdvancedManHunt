package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.constant.ConstantButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import me.supcheg.advancedmanhunt.util.reflect.Types;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JsonButtonLoreFunctionType implements FunctionalAdapterType<ConstantButtonLoreFunction> {
    public static final String NAME = "json";
    private static final String JSON = "json";

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof ConstantButtonLoreFunction;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull ConstantButtonLoreFunction value) throws IOException {
        out.name(JSON);
        GsonComponentSerializer.gson().serializer().toJson(value.getLore(), Types.type(List.class, Component.class), out);
    }

    @NotNull
    @Override
    public ConstantButtonLoreFunction read(@NotNull JsonReader in) throws IOException {
        List<Component> text = Collections.emptyList();

        if (JsonUtil.nextNonDollarName(in).equals(JSON)) {
            text = GsonComponentSerializer.gson().serializer().fromJson(in, Types.type(List.class, Component.class));
        }
        PropertyHelper.assertNonNull(text, JSON, in);

        List<Component> deserialized = text.stream()
                .map(Component::compact)
                .toList();
        return ButtonLoreFunction.constant(deserialized);
    }
}