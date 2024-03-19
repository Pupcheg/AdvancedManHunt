package me.supcheg.advancedmanhunt.gui;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.ComponentGuiTexture;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.PaperItemTexture;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryGuiController;
import me.supcheg.advancedmanhunt.gui.json.JsonGuiLoader;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.paper.BukkitUtilMock;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;

public class JsonLoadTest {

    ServerMock mock;
    InventoryGuiController guiController;

    ManHuntGameRepository gameRepository;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();
        BukkitUtilMock.mock();

        ItemStackHolder emptyItemStackHolder = Mockito.mock(ItemStackHolder.class);

        ItemStackWrapperFactory itemStackWrapperFactory = Mockito.mock(ItemStackWrapperFactory.class);
        Mockito.when(itemStackWrapperFactory.emptyItemStackHolder()).thenReturn(emptyItemStackHolder);

        ItemStackWrapper wrapper = Mockito.mock(ItemStackWrapper.class);
        Mockito.when(wrapper.createSnapshotHolder()).thenReturn(emptyItemStackHolder);

        TextureWrapper textureWrapper = Mockito.mock(TextureWrapper.class);
        Mockito.when(textureWrapper.getGuiTexture(any())).thenReturn(new ComponentGuiTexture("empty", Component.empty(), 0, 0));
        Mockito.when(textureWrapper.getPaperTexture(any())).thenReturn(new PaperItemTexture("empty", 0, 0, 0));

        ContainerAdapter containerAdapter = Mockito.mock(ContainerAdapter.class);
        Mockito.when(containerAdapter.resolveResource(any()))
                .then(inv -> Path.of("build", "resources", "main", inv.getArgument(0)));

        guiController = new InventoryGuiController(
                itemStackWrapperFactory,
                textureWrapper,
                new JsonGuiLoader(containerAdapter)
        );

        gameRepository = Mockito.mock(ManHuntGameRepository.class);
    }

    @AfterEach
    void shutdown() {
        guiController.close();
        BukkitUtilMock.unmock();
        MockBukkit.unmock();
    }

    @Test
    public void run() {
        guiController.loadResource(new GamesListGui(gameRepository), "gui/games_list.json");
    }
}
