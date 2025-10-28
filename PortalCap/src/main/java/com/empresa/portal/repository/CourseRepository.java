package com.empresa.portal.repository;

import com.empresa.portal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByModuleId(Long moduleId);
    
    List<Course> findByIsActiveTrue();
}