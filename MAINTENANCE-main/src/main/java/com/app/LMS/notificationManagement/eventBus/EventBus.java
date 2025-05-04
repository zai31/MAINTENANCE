package com.app.LMS.notificationManagement.eventBus;

public interface EventBus {
    void publish(Event event);
    void register(Class<? extends Event> eventType, EventListener<? extends Event> listener);
}
