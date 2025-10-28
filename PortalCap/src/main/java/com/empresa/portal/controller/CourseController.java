package com.empresa.portal.controller;

import com.empresa.portal.dto.ChapterDTO;
import com.empresa.portal.dto.CourseDTO;
import com.empresa.portal.service.CourseService;
import com.empresa.portal.service.ChapterService;
import com.empresa.portal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ChapterService chapterService;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            CourseDTO course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<CourseDTO>> getCoursesByModule(@PathVariable Long moduleId) {
        List<CourseDTO> courses = courseService.getCoursesByModule(moduleId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}/chapters")
    public ResponseEntity<List<ChapterDTO>> getChaptersByCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long userId) {

        try {
            List<ChapterDTO> chapters = chapterService.getChaptersByCourse(courseId);
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error cargando capítulos: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCourseWithFiles(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("moduleId") Long moduleId,
            @RequestParam(value = "instructor", required = false) String instructor,
            @RequestParam(value = "durationHours", required = false) Integer durationHours,
            @RequestParam(value = "difficulty", defaultValue = "BEGINNER") String difficulty,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "pdf", required = false) MultipartFile pdf,
            @RequestParam(value = "videoDurationMinutes", required = false) Integer videoDurationMinutes,
            @RequestParam(value = "pdfPages", required = false) Integer pdfPages,
            Authentication authentication) {
        try {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setTitle(title);
            courseDTO.setDescription(description);
            courseDTO.setModuleId(moduleId);
            courseDTO.setInstructor(instructor);
            courseDTO.setDurationHours(durationHours);
            courseDTO.setDifficulty(difficulty);
            courseDTO.setVideoDurationMinutes(videoDurationMinutes);
            courseDTO.setPdfPages(pdfPages);

            String videoFileName = null;
            String videoUrl = null;
            String pdfFileName = null;
            String pdfUrl = null;

            // Subir video si existe
            if (video != null && !video.isEmpty()) {
                videoFileName = fileStorageService.storeVideoFile(video);
                videoUrl = "/api/files/video/" + videoFileName;
                courseDTO.setVideoFileName(videoFileName);
                courseDTO.setVideoUrl(videoUrl);
                courseDTO.setHasVideo(true);
            }

            // Subir PDF si existe
            if (pdf != null && !pdf.isEmpty()) {
                pdfFileName = fileStorageService.storePdfFile(pdf);
                pdfUrl = "/api/files/pdf/" + pdfFileName;
                courseDTO.setPdfFileName(pdfFileName);
                courseDTO.setPdfUrl(pdfUrl);
                courseDTO.setHasPdf(true);
            }

            Long userId = 1L; // obtener del token JWT
            CourseDTO created = courseService.createCourse(courseDTO, userId);

            return ResponseEntity.ok(created);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCourse(
            @RequestBody CourseDTO courseDTO,
            Authentication authentication) {
        try {
            Long userId = 1L;
            CourseDTO created = courseService.createCourse(courseDTO, userId);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCourseWithFiles(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "instructor", required = false) String instructor,
            @RequestParam(value = "durationHours", required = false) Integer durationHours,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "pdf", required = false) MultipartFile pdf,
            @RequestParam(value = "removeVideo", defaultValue = "false") Boolean removeVideo,
            @RequestParam(value = "removePdf", defaultValue = "false") Boolean removePdf,
            @RequestParam(value = "videoDurationMinutes", required = false) Integer videoDurationMinutes,
            @RequestParam(value = "pdfPages", required = false) Integer pdfPages) {
        try {
            CourseDTO existingCourse = courseService.getCourseById(id);

            if (title != null)
                existingCourse.setTitle(title);
            if (description != null)
                existingCourse.setDescription(description);
            if (instructor != null)
                existingCourse.setInstructor(instructor);
            if (durationHours != null)
                existingCourse.setDurationHours(durationHours);
            if (difficulty != null)
                existingCourse.setDifficulty(difficulty);
            if (videoDurationMinutes != null)
                existingCourse.setVideoDurationMinutes(videoDurationMinutes);
            if (pdfPages != null)
                existingCourse.setPdfPages(pdfPages);

            // Manejo del video
            if (removeVideo && existingCourse.getVideoFileName() != null) {
                fileStorageService.deleteFile(existingCourse.getVideoFileName(), "video");
                existingCourse.setVideoFileName(null);
                existingCourse.setVideoUrl(null);
                existingCourse.setHasVideo(false);
            } else if (video != null && !video.isEmpty()) {
                if (existingCourse.getVideoFileName() != null) {
                    fileStorageService.deleteFile(existingCourse.getVideoFileName(), "video");
                }
                String videoFileName = fileStorageService.storeVideoFile(video);
                existingCourse.setVideoFileName(videoFileName);
                existingCourse.setVideoUrl("/api/files/video/" + videoFileName);
                existingCourse.setHasVideo(true);
            }

            // Manejo del pdf
            if (removePdf && existingCourse.getPdfFileName() != null) {
                fileStorageService.deleteFile(existingCourse.getPdfFileName(), "pdf");
                existingCourse.setPdfFileName(null);
                existingCourse.setPdfUrl(null);
                existingCourse.setHasPdf(false);
            } else if (pdf != null && !pdf.isEmpty()) {
                if (existingCourse.getPdfFileName() != null) {
                    fileStorageService.deleteFile(existingCourse.getPdfFileName(), "pdf");
                }
                String pdfFileName = fileStorageService.storePdfFile(pdf);
                existingCourse.setPdfFileName(pdfFileName);
                existingCourse.setPdfUrl("/api/files/pdf/" + pdfFileName);
                existingCourse.setHasPdf(true);
            }

            CourseDTO updated = courseService.updateCourse(id, existingCourse);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseDTO courseDTO) {
        try {
            CourseDTO updated = courseService.updateCourse(id, courseDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            CourseDTO course = courseService.getCourseById(id);

            if (course.getVideoFileName() != null) {
                fileStorageService.deleteFile(course.getVideoFileName(), "video");
            }
            if (course.getPdfFileName() != null) {
                fileStorageService.deleteFile(course.getPdfFileName(), "pdf");
            }

            courseService.deleteCourse(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Curso y archivos eliminados exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}