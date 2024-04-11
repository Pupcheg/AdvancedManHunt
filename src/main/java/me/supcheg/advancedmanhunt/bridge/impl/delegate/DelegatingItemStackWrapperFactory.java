package me.supcheg.advancedmanhunt.bridge.impl.delegate;

import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.bridge.impl.safe.BukkitItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapperFactory;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class DelegatingItemStackWrapperFactory extends DelegatingBridge<ItemStackWrapperFactory>
        implements ItemStackWrapperFactory {

    @Inject
    public DelegatingItemStackWrapperFactory() {
        super(
                NmsItemStackWrapperFactory::new,
                BukkitItemStackWrapperFactory::new
        );
    }

    @NotNull
    @Override
    public ItemStackWrapper createItemStackWrapper() {
        return delegate.createItemStackWrapper();
    }

    @NotNull
    @Override
    public ItemStackHolder emptyItemStackHolder() {
        return delegate.emptyItemStackHolder();
    }
}
