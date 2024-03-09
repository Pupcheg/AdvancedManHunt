package me.supcheg.advancedmanhunt.text.argument;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Args1<A0> {

    @NotNull
    Component build(A0 arg0);

    default void send(@NotNull CommandSender player, A0 arg0) {
        Arguments.send(player, () -> build(arg0));
    }

    default void send(@NotNull UUID uniqueId, A0 arg0) {
        Arguments.send(uniqueId, () -> build(arg0));
    }

    default void sendPlayers(@NotNull Iterable<? extends CommandSender> players, A0 arg0) {
        Arguments.sendPlayers(players, () -> build(arg0));
    }

    default void sendUniqueIds(@NotNull Iterable<UUID> uniqueIds, A0 arg0) {
        Arguments.sendUniqueIds(uniqueIds, () -> build(arg0));
    }

    default void sendNullableAndConsole(@Nullable UUID uniqueId, A0 arg0) {
        Arguments.sendNullableAndConsole(uniqueId, () -> build(arg0));
    }

    default void broadcast(A0 arg0) {
        Arguments.broadcast(() -> build(arg0));
    }
}
