package me.supcheg.advancedmanhunt.util.reflect;

import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Types {
    @NotNull
    public static Type type(@NotNull Type rawType, @NotNull Type @NotNull ... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }
}
