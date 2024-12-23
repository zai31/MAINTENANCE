package com.app.LMS.assessmentManagement.service;

import com.app.LMS.assessmentManagement.model.Question;
import com.app.LMS.assessmentManagement.model.QuestionBank;
import com.app.LMS.assessmentManagement.repository.QuestionBankRepository;
import com.app.LMS.assessmentManagement.repository.QuestionRepository;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.service.CourseService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionBankService {
    private final QuestionBankRepository questionBankRepository;
    private final CourseService courseService;
    private final QuestionRepository questionRepository;

    QuestionBankService(QuestionBankRepository questionBankRepository, CourseService courseService, QuestionRepository questionRepository) {
        this.questionBankRepository = questionBankRepository;
        this.courseService = courseService;
        this.questionRepository = questionRepository;
    }

    public void addQuestionToBank(Long courseId, Question question) {
        Course course = courseService.findCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }

        QuestionBank questionBank = questionBankRepository.findByCourseId(courseId).orElseGet(() -> {
                    QuestionBank newBank = new QuestionBank();
                    newBank.setCourse(course);
                    return questionBankRepository.save(newBank);
                });

        questionBank.getQuestions().add(question);
        questionBankRepository.save(questionBank);
    }

    public List<Question> getQuestionsByCourse(Long courseId) {
        return questionBankRepository.findByCourseId(courseId)
                .map(QuestionBank::getQuestions)
                .orElseThrow(() -> new RuntimeException("No question bank found for course"));
    }
}
