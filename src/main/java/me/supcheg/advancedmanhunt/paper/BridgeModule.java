package me.supcheg.advancedmanhunt.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import dagger.Module;
import dagger.Provides;
import me.supcheg.advancedmanhunt.injector.Injector;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;

@Module
public class BridgeModule {
    @Provides
    public ItemStackWrapperFactory itemStackWrapperFactory() {
        return Injector.getBridge().getItemStackWrapperFactory();
    }

    @Provides
    public CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher() {
        return Injector.getBridge().getGlobalCommandDispatcher();
    }
}
