package me.supcheg.advancedmanhunt.gui;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.controller.DefaultAdvancedGuiController;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.TitleSender;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;

public class JsonLoadTest {

    ServerMock mock;
    DefaultAdvancedGuiController guiController;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();

        ContainerAdapter containerAdapter = Mockito.mock(ContainerAdapter.class);
        Mockito.when(containerAdapter.resolveResource("gui/games_list.json"))
                .thenReturn(Path.of("build", "resources", "main", "gui/games_list.json"));

        guiController = new DefaultAdvancedGuiController(
                Mockito.mock(ItemStackWrapperFactory.class),
                Mockito.mock(TextureWrapper.class),
                Mockito.mock(TitleSender.class),
                containerAdapter,
                MockBukkit.createMockPlugin()
        );
    }

    @AfterEach
    void shutdown() {
        guiController.close();
        MockBukkit.unmock();
    }

    @Test
    public void run() {
        guiController.loadResource(this, "gui/games_list.json");
    }

    @SuppressWarnings({"unused", "EmptyMethod"})
    private void acceptGameButtonClick(@NotNull ButtonClickContext ctx) {
        // reflect injection
    }

    @SuppressWarnings({"unused", "EmptyMethod"})
    private void acceptGameButtonTickEnd(@NotNull ButtonResourceGetContext ctx) {
        // reflect injection
    }

    @SuppressWarnings({"unused", "EmptyMethod"})
    private void acceptGuiTickEnd(@NotNull GuiResourceGetContext ctx) {
        // reflect injection
    }
}
