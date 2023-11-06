package me.supcheg.advancedmanhunt.event;

import me.supcheg.advancedmanhunt.game.ManHuntGame;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManHuntGameCreateEvent extends ManHuntGameEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public ManHuntGameCreateEvent(@NotNull ManHuntGame manHuntGame) {
        super(manHuntGame);
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
