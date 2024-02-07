package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.constant.ConstantButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MiniMessageButtonNameFunctionType implements FunctionalAdapterType<ConstantButtonNameFunction> {
    public static final String NAME = "minimessage";
    private static final String TEXT = "text";

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof ConstantButtonNameFunction;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull ConstantButtonNameFunction value) throws IOException {
        out.name(TEXT);
        out.value(MiniMessage.miniMessage().serialize(value.getName()));
    }

    @NotNull
    @Override
    public ConstantButtonNameFunction read(@NotNull JsonReader in) throws IOException {
        String text = PropertyHelper.readString(in, TEXT);
        return ButtonNameFunction.constant(MiniMessage.miniMessage().deserialize(text).compact());
    }
}