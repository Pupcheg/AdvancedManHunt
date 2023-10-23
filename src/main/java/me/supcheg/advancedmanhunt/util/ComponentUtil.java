package me.supcheg.advancedmanhunt.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ComponentUtil {

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static List<Component> removeItalic(@Nullable List<Component> components) {
        if (components == null) {
            return null;
        }
        components.replaceAll(ComponentUtil::removeItalic);
        return components;
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static List<Component> copyAndRemoveItalic(@Nullable List<Component> components) {
        if (components == null) {
            return null;
        }
        components = new ArrayList<>(components);

        components.replaceAll(ComponentUtil::removeItalic);
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
}
