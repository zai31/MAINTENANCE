package com.app.LMS.DTO;

public class LessonRequest {

    private String title;
    private String content;
    private Long instructorId;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Long getInstructorId() {
        return this.instructorId;
    }
    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }
}
