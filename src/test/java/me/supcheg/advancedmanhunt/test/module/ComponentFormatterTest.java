package me.supcheg.advancedmanhunt.test.module;

import me.supcheg.advancedmanhunt.paper.ComponentFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComponentFormatterTest {

    private PlainTextComponentSerializer plainText;

    @BeforeEach
    void setup() {
        plainText = PlainTextComponentSerializer.plainText();
    }

    @Test
    void singleFormattingString() {
        Component component = plainText.deserialize("My message {}!!!");
        Component formatted = ComponentFormatter.format(component, "REPLACEMENT");
        String out = plainText.serialize(formatted);

        Assertions.assertEquals("My message REPLACEMENT!!!", out);
    }

    @Test
    void multipleFormattingString() {
        Component component = plainText.deserialize("My message {}!!! {}???, {}, {}");
        Component formatted = ComponentFormatter.format(component, "repl1", "repl2", "repl3", "repl4");
        String out = plainText.serialize(formatted);

        Assertions.assertEquals("My message repl1!!! repl2???, repl3, repl4", out);
    }
}
