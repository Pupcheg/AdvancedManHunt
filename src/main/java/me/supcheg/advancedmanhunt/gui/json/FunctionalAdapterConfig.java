package me.supcheg.advancedmanhunt.gui.json;

import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Objects;

@Data
public class FunctionalAdapterConfig<I> {
    private final Class<I> interfaceType;
    private final Type parameterType;
    private final Type returnType;

    private FunctionalAdapterConfig(@NotNull Class<I> interfaceType, @NotNull Type parameterType, @NotNull Type returnType) {
        this.interfaceType = interfaceType;
        this.parameterType = parameterType;
        this.returnType = returnType;
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Class<?> interfaceType;
        private Type parameterType;
        private Type returnType;

        private Builder() {
        }

        @NotNull
        @Contract("_ -> this")
        public Builder interfaceType(@NotNull Class<?> interfaceType) {
            Objects.requireNonNull(interfaceType, "interfaceType");
            this.interfaceType = interfaceType;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Builder parameterType(@NotNull Type parameterType) {
            Objects.requireNonNull(parameterType, "parameterType");
            this.parameterType = parameterType;
            return this;
        }

        @NotNull
        @Contract("_ -> this")
        public Builder returnType(@NotNull Type returnType) {
            Objects.requireNonNull(returnType, "returnType");
            this.returnType = returnType;
            return this;
        }

        @NotNull
        @Contract("-> this")
        public Builder withoutReturnType() {
            this.returnType = void.class;
            return this;
        }

        @NotNull
        @Contract(value = "-> new", pure = true)
        public <T> FunctionalAdapterConfig<T> build() {
            Objects.requireNonNull(interfaceType, "interfaceType");
            Objects.requireNonNull(parameterType, "parameterType");
            Objects.requireNonNull(returnType, "returnType");

            @SuppressWarnings("unchecked")
            FunctionalAdapterConfig<T> casted = (FunctionalAdapterConfig<T>)
                    new FunctionalAdapterConfig<>(interfaceType, parameterType, returnType);
            return casted;
        }
    }
}