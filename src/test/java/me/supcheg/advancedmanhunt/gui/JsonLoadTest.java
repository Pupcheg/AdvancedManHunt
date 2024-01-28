package me.supcheg.advancedmanhunt.gui;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.controller.DefaultAdvancedGuiController;
import me.supcheg.advancedmanhunt.structure.DummyContainerAdapter;
import me.supcheg.advancedmanhunt.util.TitleSender;
import me.supcheg.bridge.item.ItemStackHolder;
import me.supcheg.bridge.item.ItemStackWrapper;
import me.supcheg.bridge.item.ItemStackWrapperFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

public class JsonLoadTest {

    ServerMock mock;
    DefaultAdvancedGuiController guiController;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();

        ItemStackWrapperFactory itemStackWrapperFactory = new ItemStackWrapperFactory() {
            @Override
            public @NotNull ItemStackWrapper createItemStackWrapper() {
                return new ItemStackWrapper() {
                    @Override
                    public void setTitle(@NotNull Component title) {

                    }

                    @Override
                    public void setLore(@NotNull List<Component> lore) {

                    }

                    @Override
                    public void setMaterial(@NotNull String key) {

                    }

                    @Override
                    public void setCustomModelData(@Nullable Integer customModelData) {

                    }

                    @Override
                    public void setEnchanted(boolean value) {

                    }

                    @NotNull
                    @Override
                    public ItemStackHolder createSnapshotHolder() {
                        return emptyItemStackHolder();
                    }
                };
            }

            @NotNull
            @Override
            public ItemStackHolder emptyItemStackHolder() {
                return new ItemStackHolder() {
                    @Override
                    public void setAt(@NotNull Inventory inventory, int slot) {
                    }

                    @Override
                    public void sendAt(@NotNull Player player, int rawSlot) {
                    }
                };
            }
        };

        TextureWrapper textureWrapper = new TextureWrapper() {
            @Override
            public int getPaperCustomModelData(@NotNull String resourcePath) {
                return 0;
            }

            @NotNull
            @Override
            public Component getGuiBackgroundComponent(@NotNull String resourcePath) {
                return Component.empty();
            }
        };

        TitleSender titleSender = (view, title) -> {
        };
        DummyContainerAdapter containerAdapter = new DummyContainerAdapter() {
            @NotNull
            @Override
            public Path resolveResource(@NotNull String resourceName) {
                return Path.of("build", "resources", "main", resourceName);
            }
        };

        guiController = new DefaultAdvancedGuiController(
                itemStackWrapperFactory,
                textureWrapper,
                titleSender,
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
