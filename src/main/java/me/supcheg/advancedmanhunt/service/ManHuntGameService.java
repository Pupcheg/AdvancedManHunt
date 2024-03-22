package me.supcheg.advancedmanhunt.service;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.command.exception.CustomExceptions;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.player.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

import static me.supcheg.advancedmanhunt.command.exception.CommandAssertions.requireNonNull;

@RequiredArgsConstructor
public class ManHuntGameService {
    private final ManHuntGameRepository repository;

    @NotNull
    public ManHuntGame getGame(@NotNull UUID uniqueId) throws CommandSyntaxException {
        return requireNonNull(repository.getEntity(uniqueId), "game with id=" + uniqueId);
    }

    @CanIgnoreReturnValue
    @NotNull
    public ManHuntGame createGame(@NotNull UUID ownerUniqueId) {
        return repository.create(ownerUniqueId);
    }

    @NotNull
    public Collection<ManHuntGame> getAllGames() {
        return repository.getEntities();
    }

    @NotNull
    public Iterable<String> getGameStringKeys() {
        return repository.getKeys().stream().map(UUID::toString)::iterator;
    }

    public void assertCanConfigure(@NotNull CommandSender sender, @NotNull ManHuntGame game) throws CommandSyntaxException {
        if (sender instanceof Player player && !canConfigure(player, game)) {
            throw CustomExceptions.ACCESS_DENIED.create();
        }
    }

    public boolean canConfigure(@NotNull Player player, @NotNull ManHuntGame game) {
        return game.getOwner().equals(player.getUniqueId()) || player.hasPermission(Permission.CONFIGURE_ANY_GAME);
    }
}
