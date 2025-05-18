package com.app.LMS.notificationManagement.notification;

import com.app.LMS.DTO.NotificationDTO;
import com.app.LMS.common.Exceptions.dedicatedException;
import com.app.LMS.config.JwtConfig;
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
}
