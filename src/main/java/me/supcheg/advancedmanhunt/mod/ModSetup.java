package me.supcheg.advancedmanhunt.mod;

import com.google.gson.JsonParser;
import lombok.CustomLog;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@CustomLog
public class ModSetup {
    private static final String MOD_FILE = "injector.jar";
    private final Path packedPath;
    private final Path unpackedPath;

    public ModSetup(@NotNull ContainerAdapter containerAdapter) {
        this.packedPath = containerAdapter.resolveResource(MOD_FILE);
        this.unpackedPath = Path.of("mods").resolve(MOD_FILE);
    }

    @SneakyThrows
    public void setup() {
        if (!hasFabricLoader()) {
            log.error("FabricLoader wasn't detected! Installing the mod, but it won't run");
        }

        if (Files.notExists(packedPath)) {
            throw new NullPointerException(MOD_FILE + "not found in the plugin's jar");
        }

        if (notUnpackedOrOlder()) {
            Files.createDirectories(unpackedPath.getParent());
            Files.copy(packedPath, unpackedPath, StandardCopyOption.REPLACE_EXISTING);

            log.warn("FabricLoader was detected, and the {} mod unpacked in /mods. Server restart required", MOD_FILE);
            throw new IllegalStateException("Restart required");
        }
    }

    @SneakyThrows
    private boolean notUnpackedOrOlder() {
        return Files.notExists(unpackedPath) || readVersion(packedPath) > readVersion(unpackedPath);
    }

    @SneakyThrows
    private static int readVersion(@NotNull Path path) {
        try (FileSystem fs = FileSystems.newFileSystem(path)) {
            Path modJson = fs.getPath("toki.mod.json");

            String rawVersion;
            try (Reader reader = Files.newBufferedReader(modJson)) {
                rawVersion = JsonParser.parseReader(reader).getAsJsonObject()
                        .get("version").getAsString();
            }
            return Integer.parseInt(rawVersion.replace(".", ""));
        }
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
