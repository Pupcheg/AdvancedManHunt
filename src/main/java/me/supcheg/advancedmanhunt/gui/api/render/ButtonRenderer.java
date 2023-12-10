package me.supcheg.advancedmanhunt.gui.api.render;

import me.supcheg.bridge.item.ItemStackHolder;
import me.supcheg.bridge.item.ItemStackWrapperFactory;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ButtonRenderer {
    @NotNull
    ItemStackHolder render(@NotNull String texture, @NotNull Component name, @NotNull List<Component> lore, boolean enchanted);

    @NotNull
    ItemStackHolder emptyHolder();

    @NotNull
    @Contract("_, _ -> new")
    static ButtonRenderer fromTextureWrapper(@NotNull ItemStackWrapperFactory wrapperFactory, @NotNull TextureWrapper textureWrapper) {
        return new StandardButtonRenderer(wrapperFactory, textureWrapper);
    }
}
