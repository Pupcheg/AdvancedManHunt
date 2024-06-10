package me.supcheg.advancedmanhunt.io;

import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public interface IoModule {
    @Binds
    @Singleton
    ContainerAdapter containerAdapter(SimpleContainerAdapter adapter);
}
