package app.LMS.dto.dashboard;

import java.time.LocalDateTime;

public class RecentActivityDTO {
    private String activityType;
    private String description;
    private String userInvolved;
    private LocalDateTime timestamp;
    private String status;

    // Getters and Setters
    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserInvolved() {
        return userInvolved;
    }

    public void setUserInvolved(String userInvolved) {
        this.userInvolved = userInvolved;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 