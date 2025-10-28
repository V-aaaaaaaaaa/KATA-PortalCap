package com.empresa.portal.service;

import com.empresa.portal.dto.ChapterDTO;
import com.empresa.portal.dto.CourseDTO;
import com.empresa.portal.model.Course;
import com.empresa.portal.model.Module;
import com.empresa.portal.model.User;
import com.empresa.portal.repository.ChapterRepository;
import com.empresa.portal.repository.CourseRepository;
import com.empresa.portal.repository.ModuleRepository;
import com.empresa.portal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final Logger logger = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;

    @Autowired
    private ChapterService chapterService;

    public CourseService(CourseRepository courseRepository,
            ModuleRepository moduleRepository,
            UserRepository userRepository,
            ChapterRepository chapterRepository,
            ChapterService chapterService) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.userRepository = userRepository;
        this.chapterRepository = chapterRepository;
        this.chapterService = chapterService;
    }

    public List<CourseDTO> getAllCourses() {
        List<Course> list = courseRepository.findByIsActiveTrue();
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long id) throws Exception {
        Optional<Course> opt = courseRepository.findById(id);
        if (opt.isEmpty())
            throw new Exception("Curso no encontrado: " + id);
        return mapToDto(opt.get());
    }

    // Devuelve la lista de cursos para un módulo dado (activos).
    public List<CourseDTO> getCoursesByModule(Long moduleId) {
        if (moduleId == null)
            return new ArrayList<>();
        List<Course> courses = courseRepository.findByModuleId(moduleId);
        if (courses == null || courses.isEmpty())
            return new ArrayList<>();
        return courses.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO createCourse(CourseDTO dto, Long userId) throws Exception {
        logger.info("Creando curso - DTO: title={}, videoFileName={}, videoUrl={}",
                dto.getTitle(), dto.getVideoFileName(), dto.getVideoUrl());

        Course course = new Course();

        if (dto.getModuleId() != null) {
            Optional<Module> mod = moduleRepository.findById(dto.getModuleId());
            if (mod.isPresent())
                course.setModule(mod.get());
            else
                logger.warn("Module id {} no encontrado, dejando nulo", dto.getModuleId());
        }

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setInstructor(dto.getInstructor());
        course.setDurationHours(dto.getDurationHours());

        if (dto.getDifficulty() != null) {
            try {
                course.setDifficulty(Course.Difficulty.valueOf(dto.getDifficulty()));
            } catch (Exception e) {
                logger.warn("Difficulty inválida '{}', usando DEFAULT", dto.getDifficulty());
            }
        }

        // ARCHIVOS
        course.setVideoFileName(dto.getVideoFileName());
        course.setVideoUrl(dto.getVideoUrl());
        course.setVideoDurationMinutes(dto.getVideoDurationMinutes());
        course.setHasVideo(dto.getHasVideo() != null ? dto.getHasVideo() : false);

        course.setPdfFileName(dto.getPdfFileName());
        course.setPdfUrl(dto.getPdfUrl());
        course.setPdfPages(dto.getPdfPages());
        course.setHasPdf(dto.getHasPdf() != null ? dto.getHasPdf() : false);

        // Usuario creador
        if (userId != null) {
            Optional<User> u = userRepository.findById(userId);
            u.ifPresent(course::setCreatedBy);
        }

        Course saved = courseRepository.save(course);

        logger.info("Curso guardado id={} videoUrl={}", saved.getId(), saved.getVideoUrl());

        // Crear capítulos automáticamente si aplica
        createDefaultChapters(saved, dto);

        return mapToDto(saved);
    }

    // Crear capítulos automáticamente
    private void createDefaultChapters(Course course, CourseDTO courseDTO) {
        try {
            // Si hay video, crear capítulo de video
            if (courseDTO.getHasVideo() != null && courseDTO.getHasVideo()) {
                ChapterDTO videoChapter = new ChapterDTO();
                videoChapter.setCourseId(course.getId());
                videoChapter.setTitle("Video del curso");
                videoChapter.setDescription("Contenido principal en video");
                videoChapter.setOrderNumber(1);
                videoChapter.setContentType("VIDEO");
                videoChapter.setVideoFileName(courseDTO.getVideoFileName());
                videoChapter.setContentUrl(courseDTO.getVideoUrl());
                videoChapter.setDurationMinutes(
                        courseDTO.getVideoDurationMinutes() != null ? courseDTO.getVideoDurationMinutes() : 0);

                chapterService.createChapter(videoChapter);
                logger.info("Capítulo de video creado para curso id={}", course.getId());
            }

            if (courseDTO.getHasPdf() != null && courseDTO.getHasPdf()) {
                ChapterDTO pdfChapter = new ChapterDTO();
                pdfChapter.setCourseId(course.getId());

                int orderNumber = (courseDTO.getHasVideo() != null && courseDTO.getHasVideo()) ? 2 : 1;

                pdfChapter.setTitle(orderNumber == 2 ? "Material de apoyo (PDF)" : "Material del curso (PDF)");
                pdfChapter.setDescription("Documento complementario");
                pdfChapter.setOrderNumber(orderNumber);
                pdfChapter.setContentType("PDF");
                pdfChapter.setPdfFileName(courseDTO.getPdfFileName());
                pdfChapter.setContentUrl(courseDTO.getPdfUrl());

                chapterService.createChapter(pdfChapter);
                logger.info("Capítulo de PDF creado para curso id={}", course.getId());
            }
        } catch (Exception e) {
            logger.error("Error creando capítulos automáticos: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO dto) throws Exception {
        Optional<Course> opt = courseRepository.findById(id);
        if (opt.isEmpty())
            throw new Exception("Curso no encontrado: " + id);

        Course course = opt.get();

        if (dto.getTitle() != null)
            course.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            course.setDescription(dto.getDescription());
        if (dto.getInstructor() != null)
            course.setInstructor(dto.getInstructor());
        if (dto.getDurationHours() != null)
            course.setDurationHours(dto.getDurationHours());
        if (dto.getDifficulty() != null) {
            try {
                course.setDifficulty(Course.Difficulty.valueOf(dto.getDifficulty()));
            } catch (Exception e) {
                logger.warn("Difficulty inválida al actualizar: {}", dto.getDifficulty());
            }
        }

        if (dto.getVideoFileName() != null)
            course.setVideoFileName(dto.getVideoFileName());
        if (dto.getVideoUrl() != null)
            course.setVideoUrl(dto.getVideoUrl());
        if (dto.getVideoDurationMinutes() != null)
            course.setVideoDurationMinutes(dto.getVideoDurationMinutes());
        if (dto.getHasVideo() != null)
            course.setHasVideo(dto.getHasVideo());

        if (dto.getPdfFileName() != null)
            course.setPdfFileName(dto.getPdfFileName());
        if (dto.getPdfUrl() != null)
            course.setPdfUrl(dto.getPdfUrl());
        if (dto.getPdfPages() != null)
            course.setPdfPages(dto.getPdfPages());
        if (dto.getHasPdf() != null)
            course.setHasPdf(dto.getHasPdf());

        Course saved = courseRepository.save(course);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<ChapterDTO> getChaptersByCourse(Long courseId, Long userId) {
        return chapterService.getChaptersByCourse(courseId).stream()
                .map(chapterDTO -> {
                    if (userId != null) {
                    }
                    return chapterDTO;
                })
                .collect(Collectors.toList());
    }

    private CourseDTO mapToDto(Course c) {
        CourseDTO dto = new CourseDTO();
        dto.setId(c.getId());
        dto.setModuleId(c.getModule() != null ? c.getModule().getId() : null);
        dto.setModuleName(c.getModule() != null ? c.getModule().getName() : null);
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        dto.setInstructor(c.getInstructor());
        dto.setDurationHours(c.getDurationHours());
        dto.setDifficulty(c.getDifficulty() != null ? c.getDifficulty().name() : null);
        dto.setThumbnailUrl(c.getThumbnailUrl());
        dto.setIsActive(c.getIsActive());
        dto.setVideoFileName(c.getVideoFileName());
        dto.setVideoUrl(c.getVideoUrl());
        dto.setVideoDurationMinutes(c.getVideoDurationMinutes());
        dto.setPdfFileName(c.getPdfFileName());
        dto.setPdfUrl(c.getPdfUrl());
        dto.setPdfPages(c.getPdfPages());
        dto.setHasVideo(c.getHasVideo());
        dto.setHasPdf(c.getHasPdf());
        dto.setCreatedById(c.getCreatedBy() != null ? c.getCreatedBy().getId() : null);
        dto.setCreatedByName(c.getCreatedBy() != null ? c.getCreatedBy().getName() : null);
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());

        try {
            int total = c.getChapters() != null ? c.getChapters().size() : 0;
            dto.setTotalChapters(total);
        } catch (Exception e) {
        }
        return dto;
    }
}