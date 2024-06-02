package me.supcheg.advancedmanhunt.event.registry.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistration;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistry;
import me.supcheg.advancedmanhunt.paper.PluginUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Stream;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class DefaultEventListenerRegistry implements EventListenerRegistry {
    @NotNull
    @Override
    public EventListenerRegistration register(@NotNull Listener listener) {
        PluginUtil.registerEventListener(listener);
        return () ->
                findHandlers(listener.getClass())
                        .forEach(handlerList -> handlerList.unregister(listener));
    }

    @NotNull
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
