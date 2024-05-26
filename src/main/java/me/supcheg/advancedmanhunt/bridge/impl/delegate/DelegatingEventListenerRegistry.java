package me.supcheg.advancedmanhunt.bridge.impl.delegate;

import me.supcheg.advancedmanhunt.bridge.event.EventListenerRegistration;
import me.supcheg.advancedmanhunt.bridge.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsEventListenerRegistry;
import me.supcheg.advancedmanhunt.bridge.impl.safe.CustomEventListenerRegistry;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class DelegatingEventListenerRegistry extends DelegatingBridge<EventListenerRegistry>
        implements EventListenerRegistry {

    @Inject
    public DelegatingEventListenerRegistry() {
        super(
                NmsEventListenerRegistry::new,
                CustomEventListenerRegistry::new
        );
    }

    @NotNull
    @Override
    public EventListenerRegistration register(@NotNull Listener listener) {
        return delegate.register(listener);
    }
}
