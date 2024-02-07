package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.constant.ConstantGuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PathGuiBackgroundFunctionType implements FunctionalAdapterType<ConstantGuiBackgroundFunction> {
    public static final String NAME = "path";
    private static final String PATH = "path";

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof ConstantGuiBackgroundFunction;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull ConstantGuiBackgroundFunction value) throws IOException {
        out.name(PATH);
        out.value(value.getPath());
    }

    @NotNull
    @Override
    public ConstantGuiBackgroundFunction read(@NotNull JsonReader in) throws IOException {
        return GuiBackgroundFunction.constant(PropertyHelper.readString(in, PATH));
    }
}