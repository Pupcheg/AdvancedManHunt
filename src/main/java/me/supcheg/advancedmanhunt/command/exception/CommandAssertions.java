package me.supcheg.advancedmanhunt.command.exception;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.player.PermissionChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandAssertions {
    @NotNull
    @Contract("!null, _ -> param1; null, _ -> fail")
    public static <T> T requireNonNull(@Nullable T obj, @NotNull String argName) throws CommandSyntaxException {
        if (obj == null) {
            throw CustomExceptions.NULL.create(argName);
        }
        return obj;
    }

    public static void assertCanConfigure(@NotNull CommandSender sender, @NotNull ManHuntGame game) throws CommandSyntaxException {
        if (sender instanceof Player player && !PermissionChecker.canConfigure(player, game)) {
            throw CustomExceptions.ACCESS_DENIED.create();
        }
    }

    public static void assertIsRegularFile(@NotNull Path path) throws CommandSyntaxException {
        if (!Files.isRegularFile(path)) {
            throw CustomExceptions.NO_FILE.create(path);
        }
    }

    public static void assertIsDirectory(@NotNull Path path) throws CommandSyntaxException {
        if (!Files.isDirectory(path)) {
            throw CustomExceptions.NO_DIRECTORY.create(path);
        }
    }
}