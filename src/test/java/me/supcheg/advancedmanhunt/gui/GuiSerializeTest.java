package me.supcheg.advancedmanhunt.gui;

import be.seeseemelk.mockbukkit.MockBukkitExtension;
import me.supcheg.advancedmanhunt.bridge.ComponentTitleSetter;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.game.ManHuntGameService;
import me.supcheg.advancedmanhunt.game.gui.ManHuntGamesListGui;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.ComponentGuiTexture;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.PaperItemTexture;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryGuiController;
import me.supcheg.advancedmanhunt.gui.json.JsonGuiLoader;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.mock.MockBukkitUtilExtension;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith({MockBukkitExtension.class, MockBukkitUtilExtension.class})
class GuiSerializeTest {
    InventoryGuiController guiController;
    ManHuntGameService service;

    @BeforeEach
    void setup() {
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
                new JsonGuiLoader(containerAdapter),
                Mockito.mock(ComponentTitleSetter.class)
        );

        service = Mockito.mock(ManHuntGameService.class);
    }

    @AfterEach
    void shutdown() {
        guiController.close();
    }

    @Test
    public void run() {
        new ManHuntGamesListGui(service).register(guiController);
    }
}
