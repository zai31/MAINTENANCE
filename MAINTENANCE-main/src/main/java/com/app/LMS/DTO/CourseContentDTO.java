package com.app.LMS.DTO;
import java.util.List;
import com.app.LMS.DTO.LessonContentDTO;

public class CourseContentDTO {
    private Long id;
    private String title;
    private String description;
    private String duration;
    private List<LessonContentDTO> lessons;

    //getters and setters

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<LessonContentDTO> getLessons() {
        return this.lessons;
    }

    public void setLessons(List<LessonContentDTO> lessons) {
        this.lessons = lessons;
    }


}
