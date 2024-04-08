package me.supcheg.advancedmanhunt.config;

import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public interface ConfigModule {
    @Binds
    @Singleton
    Object configLoader(ConfigLoader loader);
}
