package com.app.LMS.DTO;

import java.util.List;
import java.util.Map;

public class AdminDashboardDTO {
    private Map<String, Object> platformStats;
    private List<RecentActivityDTO> recentActivities;
    private Map<String, Object> userStats;
    private List<SystemAlertDTO> systemAlerts;

    // Getters and Setters
    public Map<String, Object> getPlatformStats() {
        return platformStats;
    }

    public void setPlatformStats(Map<String, Object> platformStats) {
        this.platformStats = platformStats;
    }

    public List<RecentActivityDTO> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<RecentActivityDTO> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public Map<String, Object> getUserStats() {
        return userStats;
    }

    public void setUserStats(Map<String, Object> userStats) {
        this.userStats = userStats;
    }

    public List<SystemAlertDTO> getSystemAlerts() {
        return systemAlerts;
    }

    public void setSystemAlerts(List<SystemAlertDTO> systemAlerts) {
        this.systemAlerts = systemAlerts;
    }
} 