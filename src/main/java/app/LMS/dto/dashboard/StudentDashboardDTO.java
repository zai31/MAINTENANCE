package app.LMS.dto.dashboard;

import java.util.List;
import java.util.Map;

public class StudentDashboardDTO {
    private List<CourseOverviewDTO> enrolledCourses;
    private List<AssignmentDeadlineDTO> upcomingAssignments;
    private Map<String, Object> statistics;

    // Getters and Setters
    public List<CourseOverviewDTO> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<CourseOverviewDTO> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public List<AssignmentDeadlineDTO> getUpcomingAssignments() {
        return upcomingAssignments;
    }

    public void setUpcomingAssignments(List<AssignmentDeadlineDTO> upcomingAssignments) {
        this.upcomingAssignments = upcomingAssignments;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }
} 