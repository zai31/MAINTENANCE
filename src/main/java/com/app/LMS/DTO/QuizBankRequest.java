package com.app.LMS.DTO;

import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.Question;
import jakarta.persistence.*;

import java.util.List;

public class QuizBankRequest {
    @OneToMany
    private List<Question> questions;

    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
