package com.empresa.portal.service;

import com.empresa.portal.dto.UserProgressDTO;
import com.empresa.portal.model.Chapter;
import com.empresa.portal.model.Course;
import com.empresa.portal.model.User;
import com.empresa.portal.model.UserProgress;
import com.empresa.portal.repository.ChapterRepository;
import com.empresa.portal.repository.CourseRepository;
import com.empresa.portal.repository.UserProgressRepository;
import com.empresa.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProgressService {

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private BadgeService badgeService;

    public List<UserProgressDTO> getUserProgress(Long userId) {
        List<UserProgress> progressList = userProgressRepository.findByUserId(userId);

        return progressList.stream()
                .collect(Collectors.groupingBy(up -> up.getCourse().getId()))
                .entrySet().stream()
                .map(entry -> {
                    Long courseId = entry.getKey();
                    List<UserProgress> courseProgress = entry.getValue();

                    Course course = courseRepository.findById(courseId).orElse(null);
                    if (course == null)
                        return null;

                    long totalChapters = chapterRepository.findByCourseIdOrderByOrderNumberAsc(courseId).size();
                    long completedChapters = courseProgress.stream()
                            .filter(UserProgress::getIsCompleted)
                            .count();

                    UserProgressDTO dto = new UserProgressDTO();
                    dto.setUserId(userId);
                    dto.setCourseId(courseId);
                    dto.setCourseTitle(course.getTitle());
                    dto.setTotalChapters((int) totalChapters);
                    dto.setCompletedChapters((int) completedChapters);

                    if (totalChapters > 0) {
                        dto.setProgressPercentage((completedChapters * 100.0) / totalChapters);
                    } else {
                        dto.setProgressPercentage(0.0);
                    }

                    return dto;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserProgressDTO markChapterAsCompleted(Long userId, Long chapterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Capítulo no encontrado"));

        Course course = chapter.getCourse();

        // Buscar o crear progreso
        UserProgress progress = userProgressRepository
                .findByUserIdAndChapterId(userId, chapterId)
                .orElse(new UserProgress());

        progress.setUser(user);
        progress.setCourse(course);
        progress.setChapter(chapter);
        progress.setIsCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());

        userProgressRepository.save(progress);

        // Verificar si ya hizo todo el curso para dar insignia
        long totalChapters = chapterRepository.findByCourseIdOrderByOrderNumberAsc(course.getId()).size();
        long completedChapters = userProgressRepository
                .countByUserIdAndCourseIdAndIsCompletedTrue(userId, course.getId());

        if (totalChapters == completedChapters) {
            badgeService.awardBadgeForCourseCompletion(userId, course.getId());
        }

        UserProgressDTO dto = new UserProgressDTO();
        dto.setUserId(userId);
        dto.setCourseId(course.getId());
        dto.setChapterId(chapterId);
        dto.setIsCompleted(true);
        dto.setCourseTitle(course.getTitle());
        dto.setTotalChapters((int) totalChapters);
        dto.setCompletedChapters((int) completedChapters);

        if (totalChapters > 0) {
            dto.setProgressPercentage((completedChapters * 100.0) / totalChapters);
        }

        return dto;
    }

    @Transactional
    public void startCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Verificar si ya inicio el curso
        List<UserProgress> existingProgress = userProgressRepository
                .findByUserIdAndCourseId(userId, courseId);

        if (existingProgress.isEmpty()) {
            Chapter firstChapter = chapterRepository.findByCourseIdOrderByOrderNumberAsc(courseId)
                    .stream().findFirst().orElse(null);

            if (firstChapter != null) {
                UserProgress progress = new UserProgress();
                progress.setUser(user);
                progress.setCourse(course);
                progress.setChapter(firstChapter);
                progress.setIsCompleted(false);
                userProgressRepository.save(progress);
            }
        }
    }
}