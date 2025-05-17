package com.app.LMS.common;


import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Constants {
    public static final String ROLE_INSTRUCTOR = "INSTRUCTOR";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String UNAUTHORIZED_COURSE_ACCESS = "Unauthorized: You do not own this course";

}

