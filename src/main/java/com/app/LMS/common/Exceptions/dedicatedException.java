package com.app.LMS.common.Exceptions;

public class dedicatedException extends RuntimeException {
    public dedicatedException(String message) {
        super(message);
    }

    public dedicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    // CourseNotFoundException.java
    public static class CourseNotFoundException extends RuntimeException {
        public CourseNotFoundException(String message) {
            super(message);
        }
    }

    // LessonNotFoundException.java
    public static class LessonNotFoundException extends RuntimeException {
        public LessonNotFoundException(String message) {
            super(message);
        }
    }

    // UnauthorizedActionException.java
    public static class UnauthorizedActionException extends RuntimeException {
        public UnauthorizedActionException(String message) {
            super(message);
        }
    }

    // InvalidTokenException.java
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }

        public InvalidTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    // UserNotFoundException.java
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    // DuplicateEnrollmentException.java
    public static class DuplicateEnrollmentException extends RuntimeException {
        public DuplicateEnrollmentException(String message) {
            super(message);
        }
    }

    // NoEnrolledStudentsException.java
    public static class NoEnrolledStudentsException extends RuntimeException {
        public NoEnrolledStudentsException(String message) {
            super(message);
        }
    }

    public static class QuestionBankNotFoundException extends RuntimeException {
        public QuestionBankNotFoundException(String message) {
            super(message);
        }
    }

    public static class QuestionNotFoundException extends RuntimeException {
        public QuestionNotFoundException(String message) {
            super(message);
        }
    }

    public static class QuizNotFoundException extends RuntimeException {
        public QuizNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidQuizSubmissionException extends RuntimeException {
        public InvalidQuizSubmissionException(String message) {
            super(message);
        }
    }

    public static class InvalidCredentialsException extends dedicatedException {
        public InvalidCredentialsException(String message) {
            super(message);
        }

        public InvalidCredentialsException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
