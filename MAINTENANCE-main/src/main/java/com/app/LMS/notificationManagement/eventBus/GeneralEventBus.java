package com.app.LMS.notificationManagement.eventBus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.app.LMS.notificationManagement.eventBus.EventBus;
public class GeneralEventBus implements EventBus {
    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners = new ConcurrentHashMap<>();

    @Override
    public void publish(Event event) {
        List<EventListener<? extends Event>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener<? extends Event> listener : eventListeners) {
                notifyListener(listener, event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends Event> void notifyListener(EventListener<? extends Event> listener, Event event) {
        ((EventListener<E>) listener).handle((E) event);
    }

    @Override
    public void register(Class<? extends Event> eventType, EventListener<? extends Event> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }
}
