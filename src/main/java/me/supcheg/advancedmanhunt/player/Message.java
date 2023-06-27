package me.supcheg.advancedmanhunt.player;

import com.google.common.base.Suppliers;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Distance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Supplier;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class Message {

    public static final Args0 PREFIX = Args0.of(
            text("["),
            translatable("advancedmanhunt.prefix", NamedTextColor.RED),
            text("] ")
    );

    public static final Args1<String> COMPASS_USE = runnerName -> translatable()
            .key("advancedmanhunt.game.compass.use")
            .args(text(runnerName))
            .build();

    public static final Args0 START = Args0.of(translatable("advancedmanhunt.game.start"));

    public static final Args1<Long> START_IN = time -> translatable()
            .key("advancedmanhunt.game.start.in")
            .args(text(time))
            .build();

    public static final Args0 END = Args0.of(translatable("advancedmanhunt.game.end"));

    public static final Args1<Long> END_IN = time -> translatable()
            .key("advancedmanhunt.game.end.in")
            .args(time(time))
            .build();

    public static final Args1<String> CANCELLED_UNLOAD = worldName -> prefixed(translatable()
            .key("advancedmanhunt.region.cancelled_unload")
            .args(text(worldName, NamedTextColor.YELLOW))
            .color(NamedTextColor.RED)
            .build());

    public static final Args1<String> NO_WORLD = worldName -> prefixed(translatable()
            .key("advancedmanhunt.template.create.no_world")
            .args(text(worldName, NamedTextColor.YELLOW))
            .color(NamedTextColor.RED)
            .build());

    public static final Args1<String> CANNOT_UNLOAD = worldName -> prefixed(translatable()
            .key("advancedmanhunt.template.create.cannot_unload")
            .args(text(worldName, NamedTextColor.YELLOW))
            .color(NamedTextColor.RED)
            .build());

    public static final Args2<String, Path> CANNOT_MOVE_DATA = (worldName, path) -> prefixed(translatable()
            .key("advancedmanhunt.template.create.cannot_move_data")
            .args(text(worldName, NamedTextColor.YELLOW), absolute(path))
            .color(NamedTextColor.RED)
            .build());

    public static final Args3<String, Distance, Path> SUCCESSFUL_TEMPLATE_CREATE = (templateName, sideSize, path) -> prefixed(translatable()
            .key("advancedmanhunt.template.create.success")
            .args(text(templateName, NamedTextColor.YELLOW), regions(sideSize), absolute(path))
            .build());

    public static final Args1<Distance> SIDE_SIZE_NOT_EXACT = distance -> prefixed(translatable()
            .key("advancedmanhunt.template.create.side_size_not_exact")
            .args(text(distance.getExactRegions()))
            .color(NamedTextColor.RED)
            .build());

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static Component time(long seconds) {
        long hours = seconds / 3600;
        String raw = "%0,2d:%0,2d:%0,2d".formatted(hours, seconds / 60 - hours * 60, seconds % 60);
        return text(raw, NamedTextColor.YELLOW);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static Component absolute(@NotNull Path path) {
        return text(String.valueOf(path.toAbsolutePath()), NamedTextColor.YELLOW);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static Component regions(@NotNull Distance distance) {
        return text(distance.getRegions() + "r", NamedTextColor.YELLOW);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static Component prefixed(Component component) {
        return PREFIX.supplier.get().append(component);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Args0 {

        private final Supplier<Component> supplier;

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public static Args0 of(@NotNull Component component) {
            return new Args0(Suppliers.ofInstance(component));
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public static Args0 of(@NotNull Component... components) {
            return new Args0(Suppliers.ofInstance(Component.join(JoinConfiguration.noSeparators(), components).compact()));
        }

        public void send(@NotNull CommandSender player) {
            Message.send(player, supplier);
        }

        public void send(@NotNull ManHuntPlayerView playerView) {
            Message.send(playerView, supplier);
        }

        public void sendPlayers(@NotNull Iterable<? extends CommandSender> players) {
            Message.sendPlayers(players, supplier);
        }

        public void sendPlayerViews(@NotNull Iterable<? extends ManHuntPlayerView> playerViews) {
            Message.sendPlayerViews(playerViews, supplier);
        }

        public void broadcast() {
            Message.broadcast(supplier);
        }
    }

    public interface Args1<A0> {

        @NotNull
        Component build(A0 arg0);

        default void send(@NotNull CommandSender player, A0 arg0) {
            Message.send(player, () -> build(arg0));
        }

        default void send(@NotNull ManHuntPlayerView playerView, A0 arg0) {
            Message.send(playerView, () -> build(arg0));
        }

        default void sendPlayers(@NotNull Iterable<? extends CommandSender> players, A0 arg0) {
            Message.sendPlayers(players, () -> build(arg0));
        }

        default void sendPlayerViews(@NotNull Iterable<? extends ManHuntPlayerView> playerViews, A0 arg0) {
            Message.sendPlayerViews(playerViews, () -> build(arg0));
        }

        default void broadcast(A0 arg0) {
            Message.broadcast(() -> build(arg0));
        }

    }

    public interface Args2<A0, A1> {
        @NotNull
        Component build(A0 arg0, A1 arg1);

        default void send(@NotNull CommandSender player, A0 arg0, A1 arg1) {
            Message.send(player, () -> build(arg0, arg1));
        }

        default void send(@NotNull ManHuntPlayerView playerView, A0 arg0, A1 arg1) {
            Message.send(playerView, () -> build(arg0, arg1));
        }

        default void sendPlayers(@NotNull Iterable<? extends CommandSender> players, A0 arg0, A1 arg1) {
            Message.sendPlayers(players, () -> build(arg0, arg1));
        }

        default void sendPlayerViews(@NotNull Iterable<? extends ManHuntPlayerView> playerViews, A0 arg0, A1 arg1) {
            Message.sendPlayerViews(playerViews, () -> build(arg0, arg1));
        }

        default void broadcast(A0 arg0, A1 arg1) {
            Message.broadcast(() -> build(arg0, arg1));
        }

    }

    public interface Args3<A0, A1, A2> {
        @NotNull
        Component build(A0 arg0, A1 arg1, A2 arg2);

        default void send(@NotNull CommandSender player, A0 arg0, A1 arg1, A2 arg2) {
            Message.send(player, () -> build(arg0, arg1, arg2));
        }

        default void send(@NotNull ManHuntPlayerView playerView, A0 arg0, A1 arg1, A2 arg2) {
            Message.send(playerView, () -> build(arg0, arg1, arg2));
        }

        default void sendPlayers(@NotNull Iterable<? extends CommandSender> players, A0 arg0, A1 arg1, A2 arg2) {
            Message.sendPlayers(players, () -> build(arg0, arg1, arg2));
        }

        default void sendPlayerViews(@NotNull Iterable<? extends ManHuntPlayerView> playerViews, A0 arg0, A1 arg1, A2 arg2) {
            Message.sendPlayerViews(playerViews, () -> build(arg0, arg1, arg2));
        }

        default void broadcast(A0 arg0, A1 arg1, A2 arg2) {
            Message.broadcast(() -> build(arg0, arg1, arg2));
        }

    }

    private static void send(@NotNull CommandSender player, @NotNull Supplier<Component> supplier) {
        player.sendMessage(supplier.get());
    }

    private static void send(@NotNull ManHuntPlayerView playerView, @NotNull Supplier<Component> supplier) {
        Player player = playerView.getPlayer();
        if (player != null) {
            player.sendMessage(supplier.get());
        }
    }

    private static void sendPlayers(@NotNull Iterable<? extends CommandSender> players, @NotNull Supplier<Component> supplier) {
        Component built = null;
        for (CommandSender player : players) {
            if (built == null) {
                built = supplier.get();
            }
            player.sendMessage(built);
        }
    }

    private static void sendPlayerViews(@NotNull Iterable<? extends ManHuntPlayerView> playerViews, @NotNull Supplier<Component> supplier) {
        Component built = null;
        for (ManHuntPlayerView playerView : playerViews) {
            Player player = playerView.getPlayer();

            if (player != null) {
                if (built == null) {
                    built = supplier.get();
                }
                player.sendMessage(built);
            }
        }
    }

    private static void broadcast(@NotNull Supplier<Component> supplier) {
        Bukkit.broadcast(supplier.get().asComponent(), Permission.NOTIFICATIONS);
    }
}
