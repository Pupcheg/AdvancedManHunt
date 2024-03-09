package me.supcheg.advancedmanhunt.text;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Components {

    @NotNull
    @Contract(pure = true)
    public static Consumer<? super ComponentBuilder<?, ?>> noItalic() {
        return Components::removeItalic;
    }

    @CanIgnoreReturnValue
    @Nullable
    @Contract("_ -> param1")
    public static ComponentBuilder<?, ?> removeItalic(@Nullable ComponentBuilder<?, ?> builder) {
        return builder == null ? null : builder.decoration(TextDecoration.ITALIC, State.FALSE);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static List<Component> removeItalic(@Nullable List<Component> components) {
        if (components == null) {
            return null;
        }
        components.replaceAll(Components::removeItalic);
        return components;
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static List<Component> copyAndRemoveItalic(@Nullable List<Component> components) {
        if (components == null) {
            return null;
        }
        components = new ArrayList<>(components);

        components.replaceAll(Components::removeItalic);
        return components;
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static Component removeItalic(@Nullable Component component) {
        return component == null ? null :
                (
                        component.children().isEmpty() ?
                                removeItalicWithoutChildren(component) :
                                removeItalicWithChildren(component)
                );
    }

    @NotNull
    private static Component removeItalicWithoutChildren(@NotNull Component component) {
        return component.decorations().get(TextDecoration.ITALIC) == State.FALSE ?
                component :
                component.decoration(TextDecoration.ITALIC, State.FALSE);
    }

    @NotNull
    private static Component removeItalicWithChildren(@NotNull Component component) {
        return removeItalicWithoutChildren(component)
                .children(copyAndRemoveItalic(component.children()));
    }

    @Nullable
    public static Component deserializeWithNoItalic(@Nullable String raw) {
        return raw == null ? null : removeItalic(MiniMessage.miniMessage().deserialize(raw)).compact();
    }

    @NotNull
    public static String serializeWithNoItalicInfo(@Nullable Component component) {
        return component == null ? "null" : MiniMessage.miniMessage().serialize(component).replace("<!italic>", "");
    }
}