package me.supcheg.advancedmanhunt.bridge.impl.lazy;

import me.supcheg.advancedmanhunt.bridge.impl.safe.BukkitItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapperFactory;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class LazyItemStackWrapperFactory extends LazyBridge<ItemStackWrapperFactory>
        implements ItemStackWrapperFactory {

    @Inject
    public LazyItemStackWrapperFactory() {
        super(ItemStackWrapperFactory.class,
                NmsItemStackWrapperFactory::new,
                BukkitItemStackWrapperFactory::new
        );
    }

    @NotNull
    @Override
    public ItemStackWrapper createItemStackWrapper() {
        return delegate().createItemStackWrapper();
    }

    @NotNull
    @Override
    public ItemStackHolder emptyItemStackHolder() {
        return delegate().emptyItemStackHolder();
    }
}
