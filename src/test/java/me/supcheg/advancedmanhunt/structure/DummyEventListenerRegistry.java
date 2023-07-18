package me.supcheg.advancedmanhunt.structure;

import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class DummyEventListenerRegistry implements EventListenerRegistry {
    @Override
    public void addListener(@NotNull Listener listener) {
    }
}
