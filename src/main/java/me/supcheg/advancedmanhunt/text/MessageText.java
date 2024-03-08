package me.supcheg.advancedmanhunt.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.text.argument.Args0;
import me.supcheg.advancedmanhunt.text.argument.Args1;
import me.supcheg.advancedmanhunt.text.argument.Args2;
import me.supcheg.advancedmanhunt.text.argument.Args3;
import me.supcheg.advancedmanhunt.text.argument.Args4;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.file.Path;

import static me.supcheg.advancedmanhunt.text.Texts.path;
import static me.supcheg.advancedmanhunt.text.Texts.regions;
import static me.supcheg.advancedmanhunt.text.Texts.time;
import static me.supcheg.advancedmanhunt.text.argument.Args0.constant;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageText {

    public static final Args1<String> COMPASS_USE = runnerName -> translatable()
            .key("advancedmanhunt.game.compass.use")
            .arguments(text(runnerName))
            .build();

    public static final Args0 START = constant(translatable("advancedmanhunt.game.start"));

    public static final Args1<Long> START_IN = time -> translatable()
            .key("advancedmanhunt.game.start.in")
            .arguments(text(time))
            .build();

    public static final Args0 END = constant(translatable("advancedmanhunt.game.end"));

    public static final Args1<Long> END_IN = time -> translatable()
            .key("advancedmanhunt.game.end.in")
            .arguments(time(time))
            .build();

    public static final Args1<String> CANCELLED_UNLOAD = worldName -> translatable()
            .key("advancedmanhunt.region.cancelled_unload")
            .arguments(text(worldName, NamedTextColor.YELLOW))
            .color(NamedTextColor.RED)
            .build();

    public static final Args1<String> TEMPLATE_GENERATED_WORLD = worldName -> translatable()
            .key("advancedmanhunt.template.generate.world_success")
            .arguments(text(worldName, NamedTextColor.YELLOW))
            .build();

    public static final Args1<String> TEMPLATE_GENERATE_NO_WORLD = worldName -> translatable()
            .key("advancedmanhunt.template.generate.no_world")
            .arguments(text(worldName, NamedTextColor.YELLOW))
            .color(NamedTextColor.RED)
            .build();

    public static final Args1<String> TEMPLATE_GENERATE_CANNOT_UNLOAD = worldName -> translatable()
            .key("advancedmanhunt.template.generate.cannot_unload")
            .arguments(text(worldName, NamedTextColor.YELLOW))
            .color(NamedTextColor.RED)
            .build();

    public static final Args2<String, Path> TEMPLATE_GENERATE_CANNOT_MOVE_DATA = (worldName, path) -> translatable()
            .key("advancedmanhunt.template.generate.cannot_move_data")
            .arguments(text(worldName, NamedTextColor.YELLOW), path(path))
            .color(NamedTextColor.RED)
            .build();

    public static final Args2<String, Distance> TEMPLATE_GENERATE_START = (templateName, sideSize) -> translatable()
            .key("advancedmanhunt.template.generate.start")
            .arguments(text(templateName, NamedTextColor.YELLOW), regions(sideSize))
            .build();

    public static final Args3<String, Distance, Path> TEMPLATE_GENERATE_SUCCESS = (templateName, sideSize, path) -> translatable()
            .key("advancedmanhunt.template.generate.success")
            .arguments(text(templateName, NamedTextColor.YELLOW), regions(sideSize), path(path))
            .build();

    public static final Args1<Distance> TEMPLATE_GENERATE_RADIUS_NOT_EXACT = distance -> translatable()
            .key("advancedmanhunt.template.generate.radius_not_exact")
            .arguments(text(distance.getExactRegions()))
            .color(NamedTextColor.RED)
            .build();

    public static final Args1<String> PLUGIN_NOT_FOUND = pluginName -> translatable()
            .key("advancedmanhunt.no_plugin")
            .arguments(text(pluginName, NamedTextColor.YELLOW))
            .color(NamedTextColor.RED)
            .build();

    public static final Args1<Integer> TEMPLATE_LIST_TITLE = count -> translatable()
            .key("advancedmanhunt.template.list.title")
            .arguments(text(count, NamedTextColor.YELLOW))
            .build();

    public static final Args0 TEMPLATE_LIST_EMPTY = constant(translatable("advancedmanhunt.template.list.empty", NamedTextColor.GRAY));

    public static final Args4<String, Distance, Path, Integer> TEMPLATE_LIST_SINGLE_INFO = (name, sideSize, folder, locationsCount) -> translatable()
            .key("advancedmanhunt.template.list.info")
            .arguments(text(name, NamedTextColor.YELLOW), regions(sideSize), path(folder), text(locationsCount, NamedTextColor.YELLOW))
            .build();

    public static final Args0 TEMPLATE_IMPORT_SUCCESS = constant(translatable("advancedmanhunt.template.import.success"));

    public static final Args1<String> TEMPLATE_REMOVE_SUCCESS = name -> translatable()
            .key("advancedmanhunt.template.remove.success")
            .arguments(text(name, NamedTextColor.YELLOW))
            .build();

    public static final Args2<String, Path> TEMPLATE_EXPORT_SUCCESS = (name, path) -> translatable()
            .key("advancedmanhunt.template.export.success")
            .arguments(text(name, NamedTextColor.YELLOW), path(path))
            .build();
}
