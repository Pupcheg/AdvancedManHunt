package me.supcheg.advancedmanhunt.bridge;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.bridge.impl.lazy.LazyBrigadierCommandRegisterer;
import me.supcheg.advancedmanhunt.bridge.impl.lazy.LazyComponentTitleSetter;
import me.supcheg.advancedmanhunt.bridge.impl.lazy.LazyItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.bridge.impl.lazy.LazyKeyArgument;
import me.supcheg.advancedmanhunt.bridge.impl.lazy.LazyRegionPositionWriter;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapperFactory;

import javax.inject.Singleton;

/**
 * Bridge module contains abstractions that perform mostly unsafe operations.
 * For most of them, there are safer ones that give the most similar result.
 *
 * @see ComponentTitleSetter
 * @see ItemStackWrapperFactory
 * @see KeyArgument
 * @see RegionPositionWriter
 * @see BrigadierCommandRegisterer
 */
@Module
public interface BridgeModule {
    @Binds
    @Singleton
    ComponentTitleSetter titleSetter(LazyComponentTitleSetter titleSetter);

    @Binds
    @Singleton
    ItemStackWrapperFactory itemStackWrapperFactory(LazyItemStackWrapperFactory wrapperFactory);

    @Binds
    @Singleton
    KeyArgument keyArgument(LazyKeyArgument keyArgument);

    @Binds
    @Singleton
    RegionPositionWriter regionPositionWriter(LazyRegionPositionWriter regionPositionWriter);

    @Binds
    @Singleton
    BrigadierCommandRegisterer commandDispatcher(LazyBrigadierCommandRegisterer commandRegisterer);
}
