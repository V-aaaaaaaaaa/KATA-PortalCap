package com.empresa.portal.repository;

import com.empresa.portal.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    
    List<UserProgress> findByUserId(Long userId);
    
    List<UserProgress> findByUserIdAndCourseId(Long userId, Long courseId);
    
    Optional<UserProgress> findByUserIdAndChapterId(Long userId, Long chapterId);
    
    long countByUserIdAndCourseIdAndIsCompletedTrue(Long userId, Long courseId);
}