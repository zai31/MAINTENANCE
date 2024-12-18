package com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.service;

import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.Question;
import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.Quiz;
import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.repository.QuestionRepository;
import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.repository.QuizRepository;
import com.app.LMS.DTO.QuizRequest;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.courseManagement.repository.CourseRepository;
import com.app.LMS.userManagement.model.User;
import com.app.LMS.userManagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class QuizService {
    private final QuizRepository quizRepo;

    private final CourseRepository courseRepo;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    public QuizService(QuizRepository quizRepo, CourseRepository courseRepo, JwtConfig jwtConfig,
            UserRepository userRepository, QuestionRepository questionRepository) {
        this.quizRepo = quizRepo;
        this.courseRepo = courseRepo;
        this.jwtConfig = jwtConfig;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    public ResponseEntity<String> createQuiz(Long courseId, QuizRequest quizRequest) {
        Quiz newQuiz = new Quiz();
        newQuiz.setTitle(quizRequest.getTitle());
        newQuiz.setDuration(quizRequest.getDuration());
        newQuiz.setStartDate(quizRequest.getStartDate());
        List<Question> questions = questionRepository.saveAll(quizRequest.getQuestions());
        newQuiz.setQuestions(questions);
        newQuiz.setCourse(courseRepo.findById(courseId).orElse(null));
        quizRepo.save(newQuiz);
        return new ResponseEntity<>("Quiz created successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<String> editQuiz(long quizId, QuizRequest quizRequest) {
        // Retrieve the quiz from the database
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Update the quiz details only for the provided fields
        if (quizRequest.getTitle() != null && !quizRequest.getTitle().equals(quiz.getTitle())) {
            quiz.setTitle(quizRequest.getTitle());
        }
        if (quizRequest.getStartDate() != null) {
            quiz.setStartDate(quizRequest.getStartDate());
        }
        if (quizRequest.getDuration() != null) {
            quiz.setDuration(quizRequest.getDuration());
        }
        if (quizRequest.getQuestions() != null) {
            List<Long> questionsIds = getQuestionsIds(quiz.getQuestions());
            List<Question> questions = questionRepository.saveAll(quizRequest.getQuestions());
            quiz.setQuestions(questions);
            deleteQuestions(questionsIds);
        }
        // Save the updated quiz
        quizRepo.save(quiz);
        return new ResponseEntity<>("Course updated successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteQuiz(long quizId) {
        Quiz quiz = quizRepo.findById(quizId).orElse(null);
        assert quiz != null;
        List<Long> questionsIds = getQuestionsIds(quiz.getQuestions());
        quizRepo.deleteById(quizId);
        deleteQuestions(questionsIds);
        return new ResponseEntity<>("Course deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> validation(String token, Long courseId) {
        if (!courseRepo.existsById(courseId)) {
            return new ResponseEntity<>("Course not found", HttpStatus.NOT_FOUND);
        }
        if (!isAuthorized(token, courseId)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
        }
        return null;

    }

    public ResponseEntity<String> validation(String token, Long courseId, List<Question> questions) {
        ResponseEntity<String> error = validation(token, courseId);
        if (error != null) {
            return error;
        }
        String validQuestions = checkQuestionsValidation(questions);
        if (!validQuestions.isEmpty()) {
            return new ResponseEntity<>(validQuestions, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    public ResponseEntity<String> validation(String token, Long courseId, Long quizId) {
        ResponseEntity<String> error = validation(token, courseId);
        if (error != null) {
            return error;
        }
        if (!quizRepo.existsById(quizId)) {
            return new ResponseEntity<>("Quiz not found", HttpStatus.NOT_FOUND);
        }
        return null;
    }

    public ResponseEntity<String> validation(String token, Long courseId, Long quizId, List<Question> questions) {
        ResponseEntity<String> error = validation(token, courseId, quizId);
        if (error != null) {
            return error;
        }
        String validQuestions = checkQuestionsValidation(questions);
        if (!validQuestions.isEmpty()) {
            return new ResponseEntity<>(validQuestions, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    private String checkQuestionsValidation(List<Question> questions) {
        if (questions == null)
            return "";
        for (Question question : questions) {
            if (question.getType().name().equalsIgnoreCase("MCQ") && question.getOptions() == null) {
                return "Invalid Question, You must add options to MCQ Questions";
            } else if (!question.getType().name().equalsIgnoreCase("MCQ") && question.getOptions() != null) {
                return "Invalid Question, You must not add options to non-MCQ Questions";
            }

        }
        return "";
    }

    private boolean isAuthorized(String token, Long courseId) {
        String role = jwtConfig.getRoleFromToken(token);
        Long instructorId = jwtConfig.getUserIdFromToken(token);
        Course course = courseRepo.findById(courseId).orElse(null);
        User user = userRepository.findById(instructorId).orElse(null);
        return "INSTRUCTOR".equals(role)
                && Objects.requireNonNull(user).getId().equals(Objects.requireNonNull(course).getInstructor().getId());
    }

    List<Long> getQuestionsIds(List<Question> questions) {
        List<Long> questionsIds = new ArrayList<>();
        for (Question question : questions) {
            questionsIds.add(question.getId());
        }
        return questionsIds;
    }

    void deleteQuestions(List<Long> questionsIds) {
        for (Long questionId : questionsIds) {
            questionRepository.deleteById(questionId);
        }
    }
}
