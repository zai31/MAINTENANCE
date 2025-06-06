package com.app.LMS.assessmentManagement.service;

import com.app.LMS.DTO.*;
import com.app.LMS.assessmentManagement.model.*;
import com.app.LMS.assessmentManagement.repository.QuestionRepository;
import com.app.LMS.assessmentManagement.repository.QuizAttemptRepository;
import com.app.LMS.assessmentManagement.repository.QuizRepository;
import com.app.LMS.common.Exceptions.dedicatedException;
import com.app.LMS.courseManagement.model.Course;
import com.app.LMS.assessmentManagement.repository.QuestionBankRepository;
import com.app.LMS.courseManagement.service.CourseService;
import com.app.LMS.userManagement.model.User;
import com.app.LMS.userManagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionBankRepository questionBankRepository;
    private final CourseService courseService;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private static final String QUIZ_NOT_FOUND_MESSAGE = "Quiz not found with ID: ";

    QuizService(QuizRepository quizRepository, QuestionBankRepository questionBankRepository, CourseService courseService, UserRepository userRepository, QuestionRepository questionRepository, QuizAttemptRepository quizAttemptRepository) {
        this.quizRepository = quizRepository;
        this.questionBankRepository = questionBankRepository;
        this.courseService = courseService;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }
    // Create a new quiz for a course
    public Quiz createQuiz(QuizRequest quizRequest) {
        Course course = courseService.findCourseById(quizRequest.getCourseID());
        QuestionBank questionBank = questionBankRepository.findByCourseId(course.getId())
            .orElseThrow(() -> new dedicatedException.QuestionBankNotFoundException("Question bank not found for course ID: " + course.getId()));

        List<Question> questions = questionBank.getQuestions();
        Collections.shuffle(questions);

        // Select the desired number of questions
        List<Question> selectedQuestions = questions.stream().limit(quizRequest.getNumberOfQuestions()).toList();

        Quiz quiz = new Quiz();
        quiz.setTitle(quizRequest.getTitle());
        quiz.setStartDate(quizRequest.getStartDate());
        quiz.setDurationInMinutes(quizRequest.getDuration());
        quiz.setCourse(course);
        quiz.setQuestions(selectedQuestions);

        // Save and return the quiz
        return quizRepository.save(quiz);
    }

    // Update quiz details
    public Quiz updateQuiz(Long id, QuizRequest quizDetails) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new dedicatedException.QuizNotFoundException(QUIZ_NOT_FOUND_MESSAGE + id));        existingQuiz.setTitle(quizDetails.getTitle());
        existingQuiz.setStartDate(quizDetails.getStartDate());
        existingQuiz.setDurationInMinutes(quizDetails.getDuration());
        return quizRepository.save(existingQuiz);
    }

    // Add questions to an existing quiz
    public Quiz addQuestionsToQuiz(Long quizId, List<Question> questions) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new dedicatedException.QuizNotFoundException(QUIZ_NOT_FOUND_MESSAGE + quizId));        quiz.getQuestions().addAll(questions);
        return quizRepository.save(quiz);
    }

    // Remove a question from an existing quiz
    public boolean removeQuestionFromQuiz(Long quizId, Long questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new dedicatedException.QuizNotFoundException(QUIZ_NOT_FOUND_MESSAGE + quizId));        boolean removed = quiz.getQuestions().removeIf(question -> question.getId().equals(questionId));
        if (removed) {
            quizRepository.save(quiz);
        }
        return removed;
    }

    // Get all quizzes for a specific course
    public List<QuizResponseDTO> getQuizzesByCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId)
                .stream()
                .map(quiz -> new QuizResponseDTO(
                        quiz.getId(),
                        quiz.getTitle(),
                        quiz.getStartDate(),
                        quiz.getDurationInMinutes()
                ))
                .toList();
    }

    public QuizAttempt submitQuiz(SubmitQuizRequest request){
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new dedicatedException.QuizNotFoundException(QUIZ_NOT_FOUND_MESSAGE + request.getQuizId()));        User student = userRepository.findById(request.getStudentId())
            .orElseThrow(() -> new dedicatedException.UserNotFoundException("Student not found with ID: " + request.getStudentId()));
        if(quizAttemptRepository.existsByStudentIdAndQuizId(student.getId(), quiz.getId())){
            throw new dedicatedException.InvalidQuizSubmissionException("Student has already submitted this quiz");
        }
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setAttemptDate(new Date());

        AtomicInteger totalScore= new AtomicInteger();

        List<Answer> savedAnswers = request.getAnswers().stream().map(answerRequest -> {
            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new dedicatedException.QuestionNotFoundException("Question not found with ID: " + answerRequest.getQuestionId()));

            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setQuizAttempt(attempt);
            answer.setResponse(answerRequest.getAnswer());

            boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answerRequest.getAnswer());
            answer.setCorrect(isCorrect);

            if (isCorrect) {
                totalScore.addAndGet(question.getPoints());
            }

            return answer;
        }).toList();

        attempt.setScore(totalScore.get());
        attempt.setQuestionAnswers(savedAnswers);
        quizAttemptRepository.save(attempt);

        return attempt;

    }

    public QuizDetailsDTO getQuizDetails(Long quizId) {
        // Fetch the quiz by its ID
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new dedicatedException.QuizNotFoundException(QUIZ_NOT_FOUND_MESSAGE + quizId));
        // Map the quiz questions to QuestionResponseDTO
        List<QuestionResponseDTO> questions = quiz.getQuestions().stream()
                .map(question -> new QuestionResponseDTO(
                        question.getId(),
                        question.getQuestionText(),
                        question.getPoints(),
                        question.getOptions()
                ))
                .toList();

        // Create and return the QuizDetailsDTO
        return new QuizDetailsDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getStartDate(),
                quiz.getDurationInMinutes(),
                questions
        );
    }

    public Quiz getById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new dedicatedException.QuizNotFoundException(QUIZ_NOT_FOUND_MESSAGE + quizId));
    }



    public void autoSaveQuiz(SubmitQuizRequest autoSaveRequest) {
        Long quizId = autoSaveRequest.getQuizId();
        Long studentId = autoSaveRequest.getStudentId();

        // 1. Retrieve existing attempt or create a new draft attempt
        QuizAttempt attempt = quizAttemptRepository
                .findByQuizIdAndStudentId(quizId, studentId)
                .orElseGet(() -> {
                    QuizAttempt newAttempt = new QuizAttempt();
                    newAttempt.setQuiz(quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found")));
                    newAttempt.setStudent(userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found")));
                    newAttempt.setSubmitted(false);
                    return newAttempt;
                });

        // 2. Save progress (answers) — assuming a map of questionId -> answer is passed in SubmitQuizRequest
        attempt.setAnswers(autoSaveRequest.getAnswers());  // Or however your answers are structured
        quizAttemptRepository.save(attempt);
    }

}
