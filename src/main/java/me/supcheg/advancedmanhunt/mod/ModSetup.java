package me.supcheg.advancedmanhunt.mod;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@CustomLog
@AllArgsConstructor
public class ModSetup {
    private static final String MOD_FILE = "injector.jar";
    private final ContainerAdapter containerAdapter;

    @SneakyThrows
    public void setupIfHasFabricLoader() {
        if (!hasFabricLoader()) {
            log.error("FabricLoader wasn't detected! Any template download will fail caused by Minecraft");
            return;
        }

        Path packedModPath = containerAdapter.resolveResource(MOD_FILE);

        if (Files.notExists(packedModPath)) {
            log.error("{} not found in plugin's jar", MOD_FILE, new Exception());
            return;
        }
        Path modsDirectory = Path.of("mods");

        Path unpackedModPath = modsDirectory.resolve(MOD_FILE);
        if (Files.exists(unpackedModPath)) {
            return;
        }

        Files.createDirectories(modsDirectory);
        Files.copy(packedModPath, unpackedModPath, StandardCopyOption.REPLACE_EXISTING);
        log.warn("FabricLoader was detected, and the {} mod unpacked in /mods. Server restart required", MOD_FILE);
    }

    private static boolean hasFabricLoader() {
        try {
            Class.forName("net.fabricmc.loader.api.FabricLoader");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
