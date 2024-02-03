package me.supcheg.advancedmanhunt.injector;

import lombok.Getter;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.injector.impl.NmsBridge;
import me.supcheg.advancedmanhunt.injector.inject.RegionFileStorageInject;

public class Injector {
    @Getter
    private static Bridge bridge;

    @SneakyThrows
    public static void initialize() {
        bridge = new NmsBridge();

        new RegionFileStorageInject().inject();
    }
}
