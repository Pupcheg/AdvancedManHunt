package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.constant.ConstantButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import me.supcheg.advancedmanhunt.util.reflect.Types;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class MiniMessageButtonLoreType implements FunctionalAdapterType<ConstantButtonLoreFunction> {
    public static final String NAME = "minimessage";
    private static final String TEXT = "text";

    private final Gson gson;

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof ConstantButtonLoreFunction;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull ConstantButtonLoreFunction value) throws IOException {
        List<String> serialized = value.getLore().stream()
                .map(MiniMessage.miniMessage()::serialize)
                .toList();

        out.name(TEXT);
        gson.toJson(serialized, Types.type(List.class, String.class), out);
    }

    @NotNull
    @Override
    public ConstantButtonLoreFunction read(@NotNull JsonReader in) throws IOException {
        List<String> text = Collections.emptyList();

        if (JsonUtil.nextNonDollarName(in).equals(TEXT)) {
            text = gson.fromJson(in, Types.type(List.class, String.class));
        }
        PropertyHelper.assertNonNull(text, TEXT, in);

        List<Component> deserialized = text.stream()
                .map(MiniMessage.miniMessage()::deserialize)
                .map(Component::compact)
                .toList();
        return ButtonLoreFunction.constant(deserialized);
    }
}