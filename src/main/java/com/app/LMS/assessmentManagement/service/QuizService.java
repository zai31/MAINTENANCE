package com.app.LMS.assessmentManagement.service;

import com.app.LMS.DTO.*;
import com.app.LMS.assessmentManagement.model.*;
import com.app.LMS.assessmentManagement.repository.QuestionRepository;
import com.app.LMS.assessmentManagement.repository.QuizAttemptRepository;
import com.app.LMS.assessmentManagement.repository.QuizRepository;
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
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionBankRepository questionBankRepository;
    private final CourseService courseService;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;

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
        QuestionBank questionBank = questionBankRepository.findByCourseId(course.getId()).orElseThrow(() -> new RuntimeException("Question bank not found for the course"));

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
        Optional<Quiz> existingQuizOptional = quizRepository.findById(id);
        if (existingQuizOptional.isEmpty()) {
            return null;  // Return null if quiz not found
        }
        Quiz existingQuiz = existingQuizOptional.get();
        existingQuiz.setTitle(quizDetails.getTitle());
        existingQuiz.setStartDate(quizDetails.getStartDate());
        existingQuiz.setDurationInMinutes(quizDetails.getDuration());
        return quizRepository.save(existingQuiz);
    }

    // Add questions to an existing quiz
    public Quiz addQuestionsToQuiz(Long quizId, List<Question> questions) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        if (quizOptional.isEmpty()) {
            return null;  // Return null if quiz not found
        }
        Quiz quiz = quizOptional.get();
        quiz.getQuestions().addAll(questions);
        return quizRepository.save(quiz);
    }

    // Remove a question from an existing quiz
    public boolean removeQuestionFromQuiz(Long quizId, Long questionId) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        if (quizOptional.isEmpty()) {
            return false;  // Return false if quiz not found
        }
        Quiz quiz = quizOptional.get();
        boolean removed = quiz.getQuestions().removeIf(question -> question.getId().equals(questionId));
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
                .collect(Collectors.toList());
    }

    public QuizAttempt submitQuiz(SubmitQuizRequest request){
        Quiz quiz = quizRepository.findById(request.getQuizId()).orElseThrow(() -> new RuntimeException("Quiz not found"));
        User student = userRepository.findById(request.getStudentId()).orElseThrow(() -> new RuntimeException("Student not found"));
        if(quizAttemptRepository.existsByStudentIdAndQuizId(student.getId(), quiz.getId())){
            throw new RuntimeException("Student submitted this quiz before");
        }
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setAttemptDate(new Date());

        AtomicInteger totalScore= new AtomicInteger();

        List<Answer> savedAnswers = request.getAnswers().stream().map(answerRequest -> {
            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

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
        }).collect(Collectors.toList());

        attempt.setScore(totalScore.get());
        attempt.setQuestionAnswers(savedAnswers);
        quizAttemptRepository.save(attempt);

        return attempt;

    }

    public QuizDetailsDTO getQuizDetails(Long quizId) {
        // Fetch the quiz by its ID
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));

        // Map the quiz questions to QuestionResponseDTO
        List<QuestionResponseDTO> questions = quiz.getQuestions().stream()
                .map(question -> new QuestionResponseDTO(
                        question.getId(),
                        question.getQuestionText(),
                        question.getPoints(),
                        question.getOptions()
                ))
                .collect(Collectors.toList());

        // Create and return the QuizDetailsDTO
        return new QuizDetailsDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getStartDate(),
                quiz.getDurationInMinutes(),
                questions
        );
    }

    public Quiz getById(Long quizId)
    {
        return quizRepository.findById(quizId).orElse(null);
    }
}
