package com.app.LMS.notificationManagement.notification;

import com.app.LMS.DTO.NotificationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.from}")
    private String fromEmail;

    public NotificationService(NotificationRepository notificationRepository, JavaMailSender javaMailSender) {
        this.notificationRepository = notificationRepository;
        this.javaMailSender = javaMailSender;
    }

    public void createNotification(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public List<NotificationDTO> getNotifications(Long userId, Boolean isRead) {
        List<Notification> notifications;
        if (isRead == null) {
            // If isRead is null, return all notifications
            notifications = notificationRepository.findByUserId(userId);
        } else {
            // If isRead is true or false, filter based on 'isRead' status
            notifications = notificationRepository.findByUserIdAndIsRead(userId, isRead);
        }

        // Update the 'isRead' field to TRUE for all notifications
        notifications.forEach(notification -> {
            if (!notification.isRead()) {  // Only update if it's not already read
                notification.setRead(true);
                notificationRepository.save(notification); // Save updated notification to the database
            }
        });

        return notifications.stream()
                .map(notification -> new NotificationDTO(notification.getMessage()))
                .collect(Collectors.toList());
    }

    public void sendEmailNotification(String email, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom(fromEmail);
        javaMailSender.send(mailMessage);
    }

}
