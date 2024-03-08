package me.supcheg.advancedmanhunt.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeletingFileVisitor extends SimpleFileVisitor<Path> {
    public static final DeletingFileVisitor INSTANCE = new DeletingFileVisitor();

    @NotNull
    @Override
    public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @NotNull
    @Override
    public FileVisitResult postVisitDirectory(@NotNull Path dir, @NotNull IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }
}
