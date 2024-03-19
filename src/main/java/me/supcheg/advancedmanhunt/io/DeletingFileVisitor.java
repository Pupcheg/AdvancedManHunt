package me.supcheg.advancedmanhunt.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        super.visitFile(file, attrs);
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @NotNull
    @Override
    public FileVisitResult postVisitDirectory(@NotNull Path dir, @Nullable IOException exc) throws IOException {
        super.postVisitDirectory(dir, exc);
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }
}
