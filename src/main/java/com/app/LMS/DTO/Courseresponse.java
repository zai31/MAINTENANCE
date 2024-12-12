package com.app.LMS.DTO;

public class Courseresponse {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String duration; // Change this to a more appropriate type if needed (e.g., Integer)

    private Long instructorId; // Add this field

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }
}
