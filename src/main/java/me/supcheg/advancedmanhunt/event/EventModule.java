package me.supcheg.advancedmanhunt.event;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.registry.impl.LazyEventListenerRegistry;

import javax.inject.Singleton;

@Module
public interface EventModule {
    @Binds
    @Singleton
    EventListenerRegistry eventListenerRegistry(LazyEventListenerRegistry eventListenerRegistry);
}
