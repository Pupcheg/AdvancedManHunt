package me.supcheg.advancedmanhunt.assertion;

import be.seeseemelk.mockbukkit.command.MessageTarget;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.junit.jupiter.api.Assertions.*;

public final class MessageAssertions {

    private MessageAssertions() {
        throw new UnsupportedOperationException();
    }

    public static void assertMessagesCount(@NotNull MessageTarget messageTarget, int count) {
        for (int i = 0; i < count; i++) {
            Component message = messageTarget.nextComponentMessage();
            assertNotNull(message, "Expected messages: " + count + ", got: " + i);
        }
        messageTarget.assertNoMoreSaid();
    }

    public static void assertNextTranslatableMessagesCount(@NotNull MessageTarget target, @NotNull String key, int count) {
        for (int i = 0; i < count; i++) {
            assertNextTranslatableMessage(target, key);
        }
    }

    public static void assertNextTranslatableMessages(@NotNull MessageTarget target, @NotNull String firstKey,
                                                      @NotNull String secondKey, @NotNull String @NotNull ... otherKeys) {
        assertNextTranslatableMessage(target, firstKey);
        assertNextTranslatableMessage(target, secondKey);
        for (String otherKey : otherKeys) {
            assertNextTranslatableMessage(target, otherKey);
        }
    }

    public static void assertNextTranslatableMessage(@NotNull MessageTarget target, @NotNull String key) {
        assertEquals(key, getKeyOrThrow(target.nextComponentMessage()));
    }


    @NotNull
    private static String getKeyOrThrow(@Nullable Component component) {
        assertNotNull(component);
        assertInstanceOf(TranslatableComponent.class, component);
        return ((TranslatableComponent) component).key();
    }
}
