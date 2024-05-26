package me.supcheg.advancedmanhunt.bridge.impl.nms;

import io.papermc.paper.plugin.manager.PaperPluginManagerImpl;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.bridge.event.EventListenerRegistration;
import me.supcheg.advancedmanhunt.bridge.event.EventListenerRegistry;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static me.supcheg.advancedmanhunt.paper.BukkitUtil.getPlugin;
import static me.supcheg.advancedmanhunt.reflect.ReflectAccessors.NO_CHANGES;
import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class NmsEventListenerRegistry implements EventListenerRegistry {

    private final MethodHandle paperEventManager_createRegisteredListeners;
    private final MethodHandle paperEventManager_getRegistrationClass;
    private final MethodHandle paperEventManager_getEventListeners;

    @SneakyThrows
    public NmsEventListenerRegistry() {

        PaperPluginManagerImpl pluginManager = PaperPluginManagerImpl.getInstance();
        Object paperEventManager = NO_CHANGES.resolveFieldGetter(pluginManager.getClass(), "paperEventManager")
                .invokeExact(pluginManager);

        Class<?> paperEventManagerClass = paperEventManager.getClass();
        paperEventManager_createRegisteredListeners =
                NO_CHANGES.resolveMethodWithArgs(paperEventManagerClass,
                                "createRegisteredListeners",
                                Listener.class, Plugin.class
                        )
                        .bindTo(paperEventManager);
        paperEventManager_getRegistrationClass =
                NO_CHANGES.resolveMethodWithArgs(paperEventManagerClass,
                                "getRegistrationClass",
                                Class.class
                        )
                        .bindTo(paperEventManager);
        paperEventManager_getEventListeners =
                NO_CHANGES.resolveMethodWithArgs(paperEventManagerClass,
                                "getEventListeners",
                                Class.class
                        )
                        .bindTo(paperEventManager);
    }

    @SneakyThrows
    @NotNull
    @Override
    public EventListenerRegistration register(@NotNull Listener listener) {
        Map<Class<? extends Event>, Set<RegisteredListener>> listeners = createRegisteredListeners(listener, getPlugin());

        List<Class<? extends Event>> registrationClasses = new ArrayList<>(listeners.size());

        for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : listeners.entrySet()) {
            Class<? extends Event> registrationClass = getRegistrationClass(entry.getKey());
            registrationClasses.add(registrationClass);
            getHandlerList(registrationClass).registerAll(entry.getValue());
        }

        return () -> {
            for (Class<? extends Event> registrationClass : registrationClasses) {
                getHandlerList(registrationClass).unregister(listener);
            }
        };
    }

    @SneakyThrows
    @NotNull
    private Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
        return Objects.requireNonNull(uncheckedCast(paperEventManager_createRegisteredListeners.invokeExact(listener, plugin)));
    }

    @SneakyThrows
    @NotNull
    private Class<? extends Event> getRegistrationClass(Class<? extends Event> type) {
        return Objects.requireNonNull(uncheckedCast(paperEventManager_getRegistrationClass.invokeExact(type)));
    }

    @SneakyThrows
    @NotNull
    private HandlerList getHandlerList(@NotNull Class<? extends Event> type) {
        return (HandlerList) paperEventManager_getEventListeners.invokeExact(type);
    }
}
