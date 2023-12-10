package me.supcheg.advancedmanhunt.injector;

import me.supcheg.advancedmanhunt.injector.bridge.ModBridge;
import me.supcheg.bridge.BridgeHolder;
import net.fabricmc.api.ModInitializer;

public class ModEntry implements ModInitializer {
    @Override
    public void onInitialize() {
        BridgeHolder.setInstance(new ModBridge());
    }
}
