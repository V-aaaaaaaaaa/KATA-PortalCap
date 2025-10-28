//IMPLEMENTACION A FUTURO
package com.empresa.portal.service;

import com.empresa.portal.model.Notification;
import com.empresa.portal.model.User;
import com.empresa.portal.repository.NotificationRepository;
import com.empresa.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Notification createNotification(Long userId, String title, String message,
            Notification.NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);

        notification = notificationRepository.save(notification);
        emailService.sendNotificationEmail(user.getEmail(), user.getName(), title, message);

        return notification;
    }

    @Transactional
    public void createAchievementNotification(Long userId, String title, String message) {
        createNotification(userId, title, message, Notification.NotificationType.ACHIEVEMENT);
    }

    @Transactional
    public void createReminderNotification(Long userId, String title, String message) {
        createNotification(userId, title, message, Notification.NotificationType.REMINDER);
    }

    @Transactional
    public void createNewCourseNotification(Long userId, String title, String message) {
        createNotification(userId, title, message, Notification.NotificationType.NEW_COURSE);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }
}