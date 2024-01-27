package me.supcheg.advancedmanhunt.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.text.argument.Args0;
import me.supcheg.advancedmanhunt.text.argument.Args1;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

import static me.supcheg.advancedmanhunt.text.TextUtil.name;
import static me.supcheg.advancedmanhunt.text.argument.Args0.constant;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.TextColor.color;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GuiText {

    public static final Args0 GAMES_LIST_CREATE_NAME =
            constant(translatable("advancedmanhunt.gui.games_list.create.name", color(0xFF008C)));

    public static final Args0 GAMES_LIST_CREATE_LORE =
            constant(translatable("advancedmanhunt.gui.games_list.create.lore", NamedTextColor.GRAY));

    public static final Args0 GAMES_LIST_GAME_NAME =
            constant(translatable("advancedmanhunt.gui.games_list.game.name", NamedTextColor.RED));

    public static final Args1<GameState> GAMES_LIST_GAME_STATE = state -> translatable()
            .key("advancedmanhunt.gui.games_list.game.lore.state")
            .arguments(state.asComponent())
            .color(NamedTextColor.GRAY)
            .build();

    public static final Args1<Integer> GAMES_LIST_GAME_PLAYERS_COUNT = count -> translatable()
            .key("advancedmanhunt.gui.games_list.game.lore.players_count")
            .arguments(text(count))
            .color(NamedTextColor.GRAY)
            .build();

    public static final Args1<UUID> GAMES_LIST_GAME_OWNER = owner -> translatable()
            .key("advancedmanhunt.gui.games_list.game.lore.owner")
            .arguments(name(owner))
            .color(NamedTextColor.GRAY)
            .build();

    public static final Args1<UUID> GAMES_LIST_GAME_UNIQUE_ID = uniqueId -> translatable()
            .key("advancedmanhunt.gui.games_list.game.lore.unique_id")
            .arguments(text(uniqueId.toString().substring(0, 9)))
            .color(NamedTextColor.DARK_GRAY)
            .build();
}
