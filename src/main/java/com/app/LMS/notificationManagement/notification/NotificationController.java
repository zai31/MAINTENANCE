package com.app.LMS.notificationManagement.notification;

import com.app.LMS.DTO.NotificationDTO;
import com.app.LMS.config.JwtConfig;
import jakarta.validation.constraints.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtConfig jwtConfig;

    public NotificationController(NotificationService notificationService, JwtConfig jwtConfig) {
        this.notificationService = notificationService;
        this.jwtConfig = jwtConfig;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getNotifications(@RequestHeader("Authorization") String token, @RequestParam(value = "read", required = false) Boolean read) {
        try {
            Long userId = jwtConfig.getUserIdFromToken(token);
            List<NotificationDTO> notifications = notificationService.getNotifications(userId, read);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
