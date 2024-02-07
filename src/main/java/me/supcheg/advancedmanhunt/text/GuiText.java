package me.supcheg.advancedmanhunt.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.text.argument.Args0;
import me.supcheg.advancedmanhunt.text.argument.Args1;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import static me.supcheg.advancedmanhunt.text.TextUtil.name;
import static me.supcheg.advancedmanhunt.text.argument.Args0.constant;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GuiText {

    public static final Args0 GAME_STATE_CREATE =
            constant(translatable("advancedmanhunt.game.state.create", NamedTextColor.GOLD));

    public static final Args0 GAME_STATE_LOAD =
            constant(translatable("advancedmanhunt.game.state.load", NamedTextColor.GOLD));

    public static final Args0 GAME_STATE_START =
            constant(translatable("advancedmanhunt.game.state.start", NamedTextColor.YELLOW));

    public static final Args0 GAME_STATE_PLAY =
            constant(translatable("advancedmanhunt.game.state.play", NamedTextColor.GREEN));

    public static final Args0 GAME_STATE_STOP =
            constant(translatable("advancedmanhunt.game.state.stop", NamedTextColor.RED));

    public static final Args0 GAME_STATE_CLEAR =
            constant(translatable("advancedmanhunt.game.state.clear", NamedTextColor.AQUA));

    public static final Args0 GAME_STATE_END =
            constant(translatable("advancedmanhunt.game.state.end", NamedTextColor.GOLD));

    public static final Args0 GAME_STATE_ERROR =
            constant(translatable("advancedmanhunt.game.state.error", NamedTextColor.RED));

    public static final Map<GameState, Args0> GAME_STATE_TO_TEXT = Collections.unmodifiableMap(new EnumMap<>(Map.of(
            GameState.CREATE, GAME_STATE_CREATE,
            GameState.LOAD, GAME_STATE_LOAD,
            GameState.START, GAME_STATE_START,
            GameState.PLAY, GAME_STATE_PLAY,
            GameState.STOP, GAME_STATE_STOP,
            GameState.CLEAR, GAME_STATE_CLEAR,
            GameState.END, GAME_STATE_END,
            GameState.ERROR, GAME_STATE_ERROR
    )));

    public static final Args1<GameState> GAMES_LIST_GAME_STATE = state -> translatable()
            .key("advancedmanhunt.gui.games_list.game.lore.state")
            .arguments(GAME_STATE_TO_TEXT.get(state).build())
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
