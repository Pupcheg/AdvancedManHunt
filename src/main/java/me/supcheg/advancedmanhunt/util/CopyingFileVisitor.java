package me.supcheg.advancedmanhunt.util;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

@RequiredArgsConstructor
public class CopyingFileVisitor extends SimpleFileVisitor<Path> {
    private final Path targetPath;
    private final Path sourcePath;

    private boolean firstDirectory = true;

    @Override
    public FileVisitResult preVisitDirectory(@NotNull Path dir, @NotNull BasicFileAttributes attrs) throws IOException {
        if (firstDirectory) {
            firstDirectory = false;
        } else {
            Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir).toString()));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
        Files.copy(file, targetPath.resolve(sourcePath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
    }
}
