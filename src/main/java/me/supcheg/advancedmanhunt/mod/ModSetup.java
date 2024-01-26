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
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

@CustomLog
public class ModSetup {
    private static final String MOD_FILE = "injector.jar";
    private final Path packedPath;
    private final Path unpackedPath;

    public ModSetup(@NotNull ContainerAdapter containerAdapter) {
        this.packedPath = findInjectorJar(containerAdapter.resolveResource(""));
        this.unpackedPath = Path.of("mods").resolve(MOD_FILE);
    }

    @SneakyThrows
    @NotNull
    private static Path findInjectorJar(Path root) {
        BiPredicate<Path, BasicFileAttributes> matcher = (path, attr) -> {
            Path fileName = path.getFileName();
            return fileName != null && fileName.toString().contains("injector");
        };

        try (Stream<Path> stream = Files.find(root, 1, matcher)) {
            return stream.findFirst().orElseThrow(() -> new NullPointerException("injector jar not found in the plugin"));
        }
    }

    @SneakyThrows
    public void setup() {
        boolean hasFabricLoader = hasFabricLoader();
        if (!hasFabricLoader) {
            log.error("FabricLoader wasn't detected! Installing the mod, but it won't run");
        }

        if (notUnpackedOrOlder()) {
            Files.createDirectories(unpackedPath.getParent());
            Files.copy(packedPath, unpackedPath, StandardCopyOption.REPLACE_EXISTING);

            log.warn("FabricLoader was{} detected, and the {} mod unpacked in /mods. Server restart required",
                    hasFabricLoader ? "" : "n't", MOD_FILE);
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
            Class.forName("net.fabricmc.loader.api.FabricLoader", false, ModSetup.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
