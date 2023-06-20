package me.supcheg.advancedmanhunt.event;

import me.supcheg.advancedmanhunt.player.PlayerReturner;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PlayerReturnerInitializeEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private PlayerReturner playerReturner;

    @Nullable
    public PlayerReturner getPlayerReturner() {
        return playerReturner;
    }

    public void setPlayerReturner(@NotNull PlayerReturner playerReturner) {
        this.playerReturner = Objects.requireNonNull(playerReturner);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
