package com.app.LMS.notificationManagement.eventBus.events;

import com.app.LMS.notificationManagement.eventBus.Event;

public class QuizCreatedEvent extends Event {
    private final Long quizId;
    public QuizCreatedEvent(Long quizId) {
        this.quizId = quizId;
    }
    public Long getQuizId() {
        return this.quizId;
    }
}
