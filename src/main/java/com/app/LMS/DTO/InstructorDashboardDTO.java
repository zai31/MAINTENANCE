package com.app.LMS.DTO;

import java.util.List;
import java.util.Map;

public class InstructorDashboardDTO {
    private List<CourseOverviewDTO> teachingCourses;
    private List<AssignmentDeadlineDTO> upcomingAssignments;
    private List<StudentSubmissionDTO> recentSubmissions;
    private Map<String, Object> statistics;

    // Getters and Setters
    public List<CourseOverviewDTO> getTeachingCourses() {
        return teachingCourses;
    }

    public void setTeachingCourses(List<CourseOverviewDTO> teachingCourses) {
        this.teachingCourses = teachingCourses;
    }

    public List<AssignmentDeadlineDTO> getUpcomingAssignments() {
        return upcomingAssignments;
    }

    public void setUpcomingAssignments(List<AssignmentDeadlineDTO> upcomingAssignments) {
        this.upcomingAssignments = upcomingAssignments;
    }

    public List<StudentSubmissionDTO> getRecentSubmissions() {
        return recentSubmissions;
    }

    public void setRecentSubmissions(List<StudentSubmissionDTO> recentSubmissions) {
        this.recentSubmissions = recentSubmissions;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }
} 