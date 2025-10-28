package com.empresa.portal.controller;

import com.empresa.portal.dto.ChapterDTO;
import com.empresa.portal.service.ChapterService;
import com.empresa.portal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chapters")
@CrossOrigin(origins = "*")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ChapterDTO>> getChaptersByCourse(@PathVariable Long courseId) {
        List<ChapterDTO> chapters = chapterService.getChaptersByCourse(courseId);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChapterById(@PathVariable Long id) {
        try {
            ChapterDTO chapter = chapterService.getChapterById(id);
            return ResponseEntity.ok(chapter);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createChapter(
            @RequestParam("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("orderNumber") Integer orderNumber,
            @RequestParam("contentType") String contentType,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "durationMinutes", required = false) Integer durationMinutes) {
        try {
            ChapterDTO chapterDTO = new ChapterDTO();
            chapterDTO.setCourseId(courseId);
            chapterDTO.setTitle(title);
            chapterDTO.setDescription(description);
            chapterDTO.setOrderNumber(orderNumber);
            chapterDTO.setContentType(contentType);
            chapterDTO.setDurationMinutes(durationMinutes);

            // Valida lo del contenido del archivo
            if (file != null && !file.isEmpty()) {
                if (contentType.equals("VIDEO")) {
                    String fileName = fileStorageService.storeVideoFile(file);
                    chapterDTO.setVideoFileName(fileName);
                    chapterDTO.setContentUrl("/api/files/video/" + fileName);
                } else if (contentType.equals("PDF")) {
                    String fileName = fileStorageService.storePdfFile(file);
                    chapterDTO.setPdfFileName(fileName);
                    chapterDTO.setContentUrl("/api/files/pdf/" + fileName);
                } else {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Tipo de contenido no válido. Use VIDEO o PDF.");
                    return ResponseEntity.badRequest().body(error);
                }
            }

            ChapterDTO created = chapterService.createChapter(chapterDTO);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateChapter(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "orderNumber", required = false) Integer orderNumber,
            @RequestParam(value = "contentType", required = false) String contentType,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "durationMinutes", required = false) Integer durationMinutes,
            @RequestParam(value = "removeFile", defaultValue = "false") Boolean removeFile) {
        try {
            ChapterDTO existingChapter = chapterService.getChapterById(id);

            if (title != null)
                existingChapter.setTitle(title);
            if (description != null)
                existingChapter.setDescription(description);
            if (orderNumber != null)
                existingChapter.setOrderNumber(orderNumber);
            if (contentType != null)
                existingChapter.setContentType(contentType);
            if (durationMinutes != null)
                existingChapter.setDurationMinutes(durationMinutes);

            // Manejar archivo
            if (removeFile) {
                // Eliminar archivo existente
                if (existingChapter.getVideoFileName() != null) {
                    fileStorageService.deleteFile(existingChapter.getVideoFileName(), "video");
                    existingChapter.setVideoFileName(null);
                }
                if (existingChapter.getPdfFileName() != null) {
                    fileStorageService.deleteFile(existingChapter.getPdfFileName(), "pdf");
                    existingChapter.setPdfFileName(null);
                }
                existingChapter.setContentUrl(null);
            } else if (file != null && !file.isEmpty()) {
                if (existingChapter.getVideoFileName() != null) {
                    fileStorageService.deleteFile(existingChapter.getVideoFileName(), "video");
                    existingChapter.setVideoFileName(null);
                }
                if (existingChapter.getPdfFileName() != null) {
                    fileStorageService.deleteFile(existingChapter.getPdfFileName(), "pdf");
                    existingChapter.setPdfFileName(null);
                }

                String currentContentType = contentType != null ? contentType : existingChapter.getContentType();

                if (currentContentType.equals("VIDEO")) {
                    String fileName = fileStorageService.storeVideoFile(file);
                    existingChapter.setVideoFileName(fileName);
                    existingChapter.setContentUrl("/api/files/video/" + fileName);
                } else if (currentContentType.equals("PDF")) {
                    String fileName = fileStorageService.storePdfFile(file);
                    existingChapter.setPdfFileName(fileName);
                    existingChapter.setContentUrl("/api/files/pdf/" + fileName);
                }
            }

            ChapterDTO updated = chapterService.updateChapter(id, existingChapter);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteChapter(@PathVariable Long id) {
        try {
            // Elimina los archivos del capitulo
            ChapterDTO chapter = chapterService.getChapterById(id);
            if (chapter.getVideoFileName() != null) {
                fileStorageService.deleteFile(chapter.getVideoFileName(), "video");
            }
            if (chapter.getPdfFileName() != null) {
                fileStorageService.deleteFile(chapter.getPdfFileName(), "pdf");
            }

            // Elimina el capitulo
            chapterService.deleteChapter(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Capítulo y archivos eliminados exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Ordenar
    @PutMapping("/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reorderChapters(@RequestBody List<Map<String, Object>> chaptersOrder) {
        try {
            for (Map<String, Object> item : chaptersOrder) {
                Long chapterId = Long.valueOf(item.get("id").toString());
                Integer newOrder = Integer.valueOf(item.get("orderNumber").toString());

                ChapterDTO chapter = chapterService.getChapterById(chapterId);
                chapter.setOrderNumber(newOrder);
                chapterService.updateChapter(chapterId, chapter);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Capítulos reordenados exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}