package com.app.LMS.DTO;
import java.util.Date;

public class QuizResponseDTO {
    private Long id;
    private String title;
    private Date startDate;
    private int durationInMinutes;

    public QuizResponseDTO(Long id, String title, Date startDate, int durationInMinutes) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.durationInMinutes = durationInMinutes;
    }

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
    public Date getStartDate() {
        return this.startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public int getDurationInMinutes() {
        return this.durationInMinutes;
    }
    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }
}
