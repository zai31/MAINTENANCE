package com.app.LMS.assessmentManagement.service;

import com.app.LMS.assessmentManagement.model.Question;
import com.app.LMS.assessmentManagement.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // Create a new question
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    public Question updateQuestion(Long questionId, Question questionDetails) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found"));

        question.setQuestionText(questionDetails.getQuestionText());
        question.setType(questionDetails.getType());
        question.setPoints(questionDetails.getPoints());
        question.setCorrectAnswer(questionDetails.getCorrectAnswer());
        question.setOptions(questionDetails.getOptions());
        questionRepository.save(question);
        return question;
    }

    // Get a question by ID
    public Question getQuestionById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        return question.orElse(null);  // Return null if not found
    }

    // Delete a question by ID
    public boolean deleteQuestion(Long id) {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
            return true;
        }
        return false;  // Return false if the question doesn't exist
    }
}
