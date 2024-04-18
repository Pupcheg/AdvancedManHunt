package me.supcheg.advancedmanhunt.gui;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiLoader;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.ConfigTextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryGuiController;
import me.supcheg.advancedmanhunt.gui.json.JsonGuiLoader;

import javax.inject.Singleton;

@Module
public interface GuiModule {
    @Binds
    @Singleton
    TextureWrapper textureWrapper(ConfigTextureWrapper wrapper);

    @Binds
    @Singleton
    AdvancedGuiController guiController(InventoryGuiController controller);

    @Binds
    @Singleton
    AdvancedGuiLoader guiLoader(JsonGuiLoader loader);
}
