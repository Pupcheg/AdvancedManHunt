package me.supcheg.advancedmanhunt.bridge.impl.safe;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.bridge.event.EventListenerRegistration;
import me.supcheg.advancedmanhunt.bridge.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;

public class CustomEventListenerRegistry implements EventListenerRegistry {
    @NotNull
    @Override
    public EventListenerRegistration register(@NotNull Listener listener) {
        BukkitUtil.registerEventListener(listener);
        return () ->
                findHandlers(listener.getClass())
                        .forEach(handlerList -> handlerList.unregister(listener));
    }

    private Stream<HandlerList> findHandlers(@NotNull Class<?> clazz) {
        return Stream.concat(
                        Arrays.stream(clazz.getMethods()),
                        Arrays.stream(clazz.getDeclaredMethods())
                )
                .filter(method -> method.isAnnotationPresent(EventHandler.class))
                .filter(method -> method.getParameterCount() == 1)
                .map(method -> method.getParameterTypes()[0])
                .distinct()
                .map(this::getHandlerList);
    }

    @SneakyThrows
    private HandlerList getHandlerList(@NotNull Class<?> clazz) {
        return (HandlerList) clazz.getMethod("getHandlerList").invoke(null);
    }
}
