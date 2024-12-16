package com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.service;

import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.Question;
import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.Quiz;
import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.model.QuizBank;
import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.repository.QuestionRepository;
import com.app.LMS.AssignmentsAndQuizzesManagement.Quizzes.repository.QuizBankRepository;
import com.app.LMS.DTO.QuizBankRequest;
import com.app.LMS.DTO.QuizFromBankRequest;
import com.app.LMS.DTO.QuizRequest;
import com.app.LMS.courseManagement.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizBankService {
    private final QuizBankRepository quizBankRepository;
    private final QuizService quizService;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;

    public QuizBankService(QuizBankRepository quizBankRepository, QuizService quizService, QuestionRepository questionRepository, CourseRepository courseRepository) {
        this.quizBankRepository = quizBankRepository;
        this.quizService = quizService;
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
    }
    public ResponseEntity<String> createQuizBank(Long courseId, QuizBankRequest quizBankRequest) {
        QuizBank newQuizBank = new QuizBank();
        newQuizBank.setQuestions(questionRepository.saveAll(quizBankRequest.getQuestions()));
        newQuizBank.setCourse(courseRepository.findById(courseId).orElse(null));
        quizBankRepository.save(newQuizBank);
        return new ResponseEntity<>("Quiz Bank created successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<String> editQuizBank(Long quizBankId, QuizBankRequest quizBankRequest) {
        if(quizBankRepository.existsById(quizBankId))
        {
            // Retrieve the quiz bank from the database
            QuizBank quizBank = quizBankRepository.findById(quizBankId)
                    .orElseThrow(() -> new RuntimeException("Quiz Bank not found"));
            List<Long> questionsIds = quizService.getQuestionsIds(quizBank.getQuestions());
            List<Question> questions =  questionRepository.saveAll(quizBankRequest.getQuestions());
            quizBank.setQuestions(questions);
            quizService.deleteQuestions(questionsIds);
            return new ResponseEntity<>("Quiz Bank updated successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Quiz Bank not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> deleteQuizBank(long quizBankId) {
        if(quizBankRepository.existsById(quizBankId))
        {
            // Retrieve the quiz bank from the database
            QuizBank quizBank = quizBankRepository.findById(quizBankId).orElseThrow(() -> new RuntimeException("Quiz Bank not found")); ;
            List<Long> questionsIds = quizService.getQuestionsIds(quizBank.getQuestions());
            quizBankRepository.deleteById(quizBankId);
            quizService.deleteQuestions(questionsIds);
            return new ResponseEntity<>("Quiz Bank deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Quiz Bank not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> createQuizFromBankQuiz(QuizFromBankRequest request, long courseId, long quizBankId) {
        if(quizBankRepository.existsById(quizBankId))
        {
            // Retrieve the quiz bank from the database
            QuizBank quizBank = quizBankRepository.findById(quizBankId).orElseThrow(() -> new RuntimeException("Quiz Bank not found"));
//            List<Long> longQuestions = request.getQuestions().stream()
//                    .map(Integer::longValue)
//                    .toList();
            if(isValidQuestionsIds(quizBank.getQuestions(),request.getQuestions()))
            {
                QuizRequest quizRequest = new QuizRequest();
                quizRequest.setDuration(request.getDuration());
                quizRequest.setTitle(request.getTitle());
                quizRequest.setStartDate(request.getStartDate());
                quizRequest.setQuestions(createNewQuestions(request.getQuestions()));
                return quizService.createQuiz(courseId,quizRequest);
            }
            else
            {
                return new ResponseEntity<>("Question Id is not found", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("Quiz Bank not found", HttpStatus.NOT_FOUND);
    }


    private List<Question> createNewQuestions(List<Long> questionsIds) {
        List<Question> questions = questionRepository.findAllById(questionsIds);
        List<Question> newQuestions = new ArrayList<>();
        for (Question question : questions) {
            Question newQuestion = new Question();
            List<String> options =  new ArrayList<>();
            for (String option : question.getOptions()) {
                options.add(option);
            }
            newQuestion.setQuestion(question.getQuestion());
            newQuestion.setAnswer(question.getAnswer());
            newQuestion.setOptions(options);
            newQuestion.setPoints(question.getPoints());
            newQuestion.setType(question.getType());
            newQuestions.add(newQuestion);
        }
    return newQuestions;
    }
    private boolean isValidQuestionsIds(List<Question> questions,List<Long> questionsIds) {
        List<Long> ids = quizService.getQuestionsIds(questions);

        for (Long questionId : questionsIds) {
            if(!ids.contains(questionId))
            {
                 return  false;
            }
        }
        return true;
    }


    public ResponseEntity<String> validation(String token,Long courseId,List<Question> questions) {
        return quizService.validation(token,courseId,questions);
    }
    public ResponseEntity<String> validation(String token,Long courseId,Long quizBankId,List<Question> questions) {
        ResponseEntity<String> error = quizService.validation(token,courseId,questions);
        if (error != null) return error;
        if(!quizBankRepository.existsById(quizBankId)){
            return new ResponseEntity<>("Quiz bank not found", HttpStatus.NOT_FOUND);
        }
        return null;
    }
    public ResponseEntity<String> validation(String token,Long courseId,Long quizBankId) {
        ResponseEntity<String> error = quizService.validation(token,courseId);
        if (error != null) return error;
        if(!quizBankRepository.existsById(quizBankId)){
            return new ResponseEntity<>("Quiz bank not found", HttpStatus.NOT_FOUND);
        }
        return null;
    }
}
