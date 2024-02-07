package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.constant.ConstantButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JsonButtonNameFunctionType implements FunctionalAdapterType<ConstantButtonNameFunction> {
    public static final String NAME = "json";
    private static final String JSON = "json";

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof ConstantButtonNameFunction;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull ConstantButtonNameFunction value) throws IOException {
        out.name(JSON);
        GsonComponentSerializer.gson().serializer().toJson(value.getName(), Component.class, out);
    }

    @NotNull
    @Override
    public ConstantButtonNameFunction read(@NotNull JsonReader in) throws IOException {
        Component text = null;

        if (JsonUtil.nextNonDollarName(in).equals(JSON)) {
            text = GsonComponentSerializer.gson().serializer().fromJson(in, Component.class);
        }
        PropertyHelper.assertNonNull(text, JSON, in);

        return ButtonNameFunction.constant(text.compact());
    }
}