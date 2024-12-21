package com.app.LMS.DTO;

public class NotificationDTO {
    private String message;

    public NotificationDTO(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
