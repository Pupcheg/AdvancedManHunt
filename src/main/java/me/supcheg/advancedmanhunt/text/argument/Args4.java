package me.supcheg.advancedmanhunt.text.argument;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Args4<A0, A1, A2, A3> {
    @NotNull
    Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);

    default void send(@NotNull CommandSender player, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
        Arguments.send(player, () -> build(arg0, arg1, arg2, arg3));
    }

    default void send(@NotNull UUID uniqueId, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
        Arguments.send(uniqueId, () -> build(arg0, arg1, arg2, arg3));
    }

    default void sendPlayers(@NotNull Iterable<? extends CommandSender> players, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
        Arguments.sendPlayers(players, () -> build(arg0, arg1, arg2, arg3));
    }

    default void sendUniqueIds(@NotNull Iterable<UUID> uniqueIds, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
        Arguments.sendUniqueIds(uniqueIds, () -> build(arg0, arg1, arg2, arg3));
    }

    default void sendNullableAndConsole(@Nullable UUID uniqueId, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
        Arguments.sendNullableAndConsole(uniqueId, () -> build(arg0, arg1, arg2, arg3));
    }

    default void broadcast(A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
        Arguments.broadcast(() -> build(arg0, arg1, arg2, arg3));
    }

}
