package com.app.LMS.notificationManagement.eventBus;

public interface EventListener<E extends Event> {
    void handle(E event);
}
