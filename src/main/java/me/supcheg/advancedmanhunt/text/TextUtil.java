package me.supcheg.advancedmanhunt.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.text.argument.Args1;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtil {
    public static final Args1<Integer> SINGLE_END_REGION = regions -> translatable()
            .key("advancedmanhunt.region.single")
            .arguments(text(regions))
            .color(NamedTextColor.YELLOW)
            .build();

    public static final Args1<Integer> TWO_END_REGION = regions -> translatable()
            .key("advancedmanhunt.region.two")
            .arguments(text(regions))
            .color(NamedTextColor.YELLOW)
            .build();

    public static final Args1<Integer> MULTIPLE_END_REGIONS = regions -> translatable()
            .key("advancedmanhunt.region.multi")
            .arguments(text(regions))
            .color(NamedTextColor.YELLOW)
            .build();

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Component time(long seconds) {
        long hours = seconds / 3600;
        String raw = "%0,2d:%0,2d:%0,2d".formatted(hours, seconds / 60 - hours * 60, seconds % 60);
        return text(raw, NamedTextColor.YELLOW);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Component path(@NotNull Path path) {
        return text(path.toString(), NamedTextColor.YELLOW);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Component regions(@NotNull Distance distance) {
        int regions = distance.getRegions();

        Args1<Integer> message = switch (regions % 10) {
            case 1 -> SINGLE_END_REGION;
            case 2, 3, 4 -> TWO_END_REGION;
            default -> MULTIPLE_END_REGIONS;
        };

        return message.build(regions);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Component name(@NotNull UUID playerUniqueId) {
        String name = Bukkit.getOfflinePlayer(playerUniqueId).getName();
        return name == null ? Component.text("{" + playerUniqueId + "}") : Component.text(name);
    }
}
