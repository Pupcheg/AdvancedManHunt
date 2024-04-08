package me.supcheg.advancedmanhunt.game.handler;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public abstract class ManHuntGameHandler implements Listener {
    protected final ManHuntGame game;

    public void register() {
        BukkitUtil.registerEventListener(this);
    }

    public abstract void unregister();

    protected boolean shouldHandleAt(@NotNull Location location) {
        RealEnvironment environment = RealEnvironment.fromBukkit(location.getWorld().getEnvironment());
        return game.getRegion(environment).contains(location);
    }

    protected boolean isPlaying() {
        return game.getState().ordinal() > GameState.CREATE.ordinal();
    }
}
