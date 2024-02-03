package me.supcheg.advancedmanhunt.injector.item;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface ItemStackWrapperFactory {
    @Contract(value = "-> new", pure = true)
    @NotNull
    ItemStackWrapper createItemStackWrapper();

    @Contract(pure = true)
    @NotNull
    ItemStackHolder emptyItemStackHolder();
}
