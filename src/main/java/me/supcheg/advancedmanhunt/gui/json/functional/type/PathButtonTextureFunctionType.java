package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.constant.ConstantButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PathButtonTextureFunctionType implements FunctionalAdapterType<ConstantButtonTextureFunction> {
    public static final String NAME = "path";
    private static final String PATH = "path";

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof ConstantButtonTextureFunction;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull ConstantButtonTextureFunction value) throws IOException {
        out.name(PATH);
        out.value(value.getPath());
    }

    @NotNull
    @Override
    public ConstantButtonTextureFunction read(@NotNull JsonReader in) throws IOException {
        return ButtonTextureFunction.constant(PropertyHelper.readString(in, PATH));
    }
}