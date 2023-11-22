package me.supcheg.advancedmanhunt.event;

import lombok.Getter;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class ManHuntGameEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final ManHuntGame manHuntGame;

    protected ManHuntGameEvent(@NotNull ManHuntGame manHuntGame) {
        this.manHuntGame = manHuntGame;
    }

    protected ManHuntGameEvent(@NotNull ManHuntGame manHuntGame, boolean isAsync) {
        super(isAsync);
        this.manHuntGame = manHuntGame;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
