package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.command.MessageTarget;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class MessageUtil {

    private MessageUtil() {
        throw new UnsupportedOperationException();
    }

    public static void assertMessagesCount(@NotNull MessageTarget messageTarget, int count) {
        for (int i = 0; i < count; i++) {
            Component message = messageTarget.nextComponentMessage();
            assertNotNull(message, "Expected messages: " + count + ", got: " + i);
        }
        messageTarget.assertNoMoreSaid();
    }
}
