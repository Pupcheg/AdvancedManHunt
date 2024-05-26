package me.supcheg.advancedmanhunt.bridge;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.bridge.command.EnumArgument;
import me.supcheg.advancedmanhunt.bridge.command.KeyArgument;
import me.supcheg.advancedmanhunt.bridge.command.UniqueIdArgument;
import me.supcheg.advancedmanhunt.bridge.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.bridge.impl.delegate.DelegatingBrigadierCommandRegisterer;
import me.supcheg.advancedmanhunt.bridge.impl.delegate.DelegatingComponentTitleSetter;
import me.supcheg.advancedmanhunt.bridge.impl.delegate.DelegatingEnumArgument;
import me.supcheg.advancedmanhunt.bridge.impl.delegate.DelegatingEventListenerRegistry;
import me.supcheg.advancedmanhunt.bridge.impl.delegate.DelegatingItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.bridge.impl.delegate.DelegatingKeyArgument;
import me.supcheg.advancedmanhunt.bridge.impl.delegate.DelegatingRegionPositionWriter;
import me.supcheg.advancedmanhunt.bridge.impl.delegate.DelegatingUniqueIdArgument;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapperFactory;

import javax.inject.Singleton;

/**
 * Bridge module contains abstractions that perform mostly unsafe operations.
 * For most of them, there are safer ones that give the most similar result.
 *
 * @see ComponentTitleSetter
 * @see ItemStackWrapperFactory
 * @see KeyArgument
 * @see UniqueIdArgument
 * @see EnumArgument
 * @see RegionPositionWriter
 * @see BrigadierCommandRegisterer
 * @see EventListenerRegistry
 */
@Module
public interface BridgeModule {
    @Binds
    @Singleton
    ComponentTitleSetter titleSetter(DelegatingComponentTitleSetter titleSetter);

    @Binds
    @Singleton
    ItemStackWrapperFactory itemStackWrapperFactory(DelegatingItemStackWrapperFactory wrapperFactory);

    @Binds
    @Singleton
    KeyArgument keyArgument(DelegatingKeyArgument keyArgument);

    @Binds
    @Singleton
    UniqueIdArgument uniqueIdArgument(DelegatingUniqueIdArgument uniqueIdArgument);

    @Binds
    @Singleton
    EnumArgument enumArgument(DelegatingEnumArgument enumArgument);

    @Binds
    @Singleton
    RegionPositionWriter regionPositionWriter(DelegatingRegionPositionWriter regionPositionWriter);

    @Binds
    @Singleton
    BrigadierCommandRegisterer commandDispatcher(DelegatingBrigadierCommandRegisterer commandRegisterer);

    @Binds
    @Singleton
    EventListenerRegistry eventListenerRegistry(DelegatingEventListenerRegistry eventListenerRegistry);
}
