package me.supcheg.advancedmanhunt.game.handler;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistration;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistry;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.region.RealEnvironment.environment;

@RequiredArgsConstructor
public abstract class ManHuntGameHandler implements Listener {
    private EventListenerRegistration eventListenerRegistration;
    protected final ManHuntGame game;

    public void registerWith(@NotNull EventListenerRegistry registry) {
        eventListenerRegistration = registry.register(this);
    }

    public void unregister() {
        if (eventListenerRegistration != null) {
            eventListenerRegistration.unregister();
        }
    }

    protected boolean shouldHandleAt(@NotNull Location location) {
        RealEnvironment environment = environment(location.getWorld());
        return game.getRegion(environment).positionSource().box().includes(location);
    }

    protected boolean isPlaying() {
        return game.getState().ordinal() > GameState.CREATE.ordinal();
    }
}
