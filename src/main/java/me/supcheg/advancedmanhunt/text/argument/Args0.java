package me.supcheg.advancedmanhunt.text.argument;

import com.google.common.base.Suppliers;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Args0 {
    private final Supplier<Component> supplier;

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Args0 constant(@NotNull Component component) {
        return new Args0(Suppliers.ofInstance(component.compact()));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Args0 constant(@NotNull Component... components) {
        return constant(Component.join(JoinConfiguration.noSeparators(), components));
    }

    @NotNull
    public Component build() {
        return supplier.get();
    }

    public void send(@NotNull CommandSender player) {
        ArgumentsService.send(player, supplier);
    }

    public void send(@NotNull UUID uniqueId) {
        ArgumentsService.send(uniqueId, supplier);
    }

    public void sendPlayers(@NotNull Iterable<? extends CommandSender> players) {
        ArgumentsService.sendPlayers(players, supplier);
    }

    public void sendUniqueIds(@NotNull Iterable<UUID> uniqueIds) {
        ArgumentsService.sendUniqueIds(uniqueIds, supplier);
    }

    public void broadcast() {
        ArgumentsService.broadcast(supplier);
    }
}
