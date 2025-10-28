package com.empresa.portal.service;

import com.empresa.portal.dto.ChapterDTO;
import com.empresa.portal.model.Chapter;
import com.empresa.portal.model.Course;
import com.empresa.portal.repository.ChapterRepository;
import com.empresa.portal.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<ChapterDTO> getChaptersByCourse(Long courseId) {
        return chapterRepository.findByCourseIdOrderByOrderNumberAsc(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ChapterDTO getChapterById(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Capítulo no encontrado"));
        return convertToDTO(chapter);
    }

    @Transactional
    public ChapterDTO createChapter(ChapterDTO chapterDTO) {
        Course course = courseRepository.findById(chapterDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Chapter chapter = new Chapter();
        chapter.setCourse(course);
        chapter.setTitle(chapterDTO.getTitle());
        chapter.setDescription(chapterDTO.getDescription());
        chapter.setOrderNumber(chapterDTO.getOrderNumber());
        chapter.setContentType(Chapter.ContentType.valueOf(chapterDTO.getContentType()));
        chapter.setContentUrl(chapterDTO.getContentUrl());
        chapter.setVideoFileName(chapterDTO.getVideoFileName());
        chapter.setPdfFileName(chapterDTO.getPdfFileName());
        chapter.setDurationMinutes(chapterDTO.getDurationMinutes());

        chapter = chapterRepository.save(chapter);
        return convertToDTO(chapter);
    }

    @Transactional
    public ChapterDTO updateChapter(Long chapterId, ChapterDTO chapterDTO) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Capítulo no encontrado"));

        if (chapterDTO.getTitle() != null) {
            chapter.setTitle(chapterDTO.getTitle());
        }
        if (chapterDTO.getDescription() != null) {
            chapter.setDescription(chapterDTO.getDescription());
        }
        if (chapterDTO.getOrderNumber() != null) {
            chapter.setOrderNumber(chapterDTO.getOrderNumber());
        }
        if (chapterDTO.getContentType() != null) {
            chapter.setContentType(Chapter.ContentType.valueOf(chapterDTO.getContentType()));
        }
        if (chapterDTO.getContentUrl() != null) {
            chapter.setContentUrl(chapterDTO.getContentUrl());
        }
        if (chapterDTO.getVideoFileName() != null) {
            chapter.setVideoFileName(chapterDTO.getVideoFileName());
        }
        if (chapterDTO.getPdfFileName() != null) {
            chapter.setPdfFileName(chapterDTO.getPdfFileName());
        }
        if (chapterDTO.getDurationMinutes() != null) {
            chapter.setDurationMinutes(chapterDTO.getDurationMinutes());
        }

        chapter = chapterRepository.save(chapter);
        return convertToDTO(chapter);
    }

    @Transactional
    public void deleteChapter(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Capítulo no encontrado"));
        chapterRepository.delete(chapter);
    }

    private ChapterDTO convertToDTO(Chapter chapter) {
        ChapterDTO dto = new ChapterDTO();
        dto.setId(chapter.getId());
        dto.setCourseId(chapter.getCourse().getId());
        dto.setTitle(chapter.getTitle());
        dto.setDescription(chapter.getDescription());
        dto.setOrderNumber(chapter.getOrderNumber());
        dto.setContentType(chapter.getContentType().name());
        dto.setContentUrl(chapter.getContentUrl());
        dto.setVideoFileName(chapter.getVideoFileName());
        dto.setPdfFileName(chapter.getPdfFileName());
        dto.setDurationMinutes(chapter.getDurationMinutes());
        dto.setIsCompleted(false); // se consulta con userId
        return dto;
    }
}