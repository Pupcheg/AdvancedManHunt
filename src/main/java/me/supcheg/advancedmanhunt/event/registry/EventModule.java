package me.supcheg.advancedmanhunt.event.registry;

import dagger.Binds;
import dagger.Module;
import me.supcheg.advancedmanhunt.event.registry.impl.DefaultEventListenerRegistry;

import javax.inject.Singleton;

@Module
public interface EventModule {
    @Binds
    @Singleton
    EventListenerRegistry eventListenerRegistry(DefaultEventListenerRegistry eventListenerRegistry);
}
