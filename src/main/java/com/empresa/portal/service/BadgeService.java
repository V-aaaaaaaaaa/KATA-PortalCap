package com.empresa.portal.service;

import com.empresa.portal.model.Badge;
import com.empresa.portal.model.Course;
import com.empresa.portal.model.UserBadge;
import com.empresa.portal.repository.BadgeRepository;
import com.empresa.portal.repository.CourseRepository;
import com.empresa.portal.repository.UserBadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Otorgar insignia al usuario al completar un curso
    @Transactional
    public void awardBadgeForCourseCompletion(Long userId, Long courseId) {
        try {
            // saber el modulo del curso
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

            Long moduleId = course.getModule().getId();

            // Busca la insignia del modulo
            Optional<Badge> badgeOpt = badgeRepository.findByModuleId(moduleId);
            if (badgeOpt.isEmpty()) {
                System.out.println("No hay insignia configurada para el módulo: " + moduleId);
                return;
            }

            Badge badge = badgeOpt.get();

            // Verificar si ya tiene la insignia
            Optional<UserBadge> existingBadge = userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId());
            if (existingBadge.isPresent()) {
                System.out.println("Usuario ya tiene esta insignia");
                return;
            }

            // Le da la insignia
            UserBadge userBadge = new UserBadge();
            userBadge.setUserId(userId);
            userBadge.setBadgeId(badge.getId());
            userBadge.setCourseId(courseId);
            userBadge.setEarnedAt(LocalDateTime.now());

            userBadgeRepository.save(userBadge);

            System.out.println(" Insignia otorgada: " + badge.getName() + " a usuario: " + userId);

        } catch (Exception e) {
            System.err.println("Error al otorgar insignia: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserIdWithBadge(userId);
    }

    // Si el usuario tiene una insignia específica
    public boolean userHasBadge(Long userId, Long badgeId) {
        return userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId).isPresent();
    }
}