package com.app.LMS.notificationManagement.notification;

import com.app.LMS.DTO.NotificationDTO;
import com.app.LMS.common.Exceptions.dedicatedException;
import com.app.LMS.config.JwtConfig;
import com.app.LMS.notificationManagement.eventBus.EventBus;
import com.app.LMS.notificationManagement.eventBus.events.AssignmentDeadlineReminderEvent;
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
    private final EventBus eventBus;

    public NotificationController(NotificationService notificationService, JwtConfig jwtConfig,EventBus eventBus) {
        this.notificationService = notificationService;
        this.jwtConfig = jwtConfig;
        this.eventBus = eventBus;
    }

    @GetMapping("/list")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "read", required = false) Boolean read) {
        try {
            Long userId = jwtConfig.getUserIdFromToken(token);
            List<NotificationDTO> notifications = notificationService.getNotifications(userId, read);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (dedicatedException.UnauthorizedActionException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/test-deadline-reminder")
    public ResponseEntity<String> testReminder(@RequestParam Long assignmentId) {
        eventBus.publish(new AssignmentDeadlineReminderEvent(assignmentId));
        return ResponseEntity.ok("Reminder triggered for assignment ID: " + assignmentId);
    }

}
