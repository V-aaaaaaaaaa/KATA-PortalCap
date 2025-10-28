package com.empresa.portal.controller;

import com.empresa.portal.dto.UserProgressDTO;
import com.empresa.portal.service.UserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class UserProgressController {

    @Autowired
    private UserProgressService userProgressService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserProgressDTO>> getUserProgress(@PathVariable Long userId) {
        List<UserProgressDTO> progress = userProgressService.getUserProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/chapter/complete")
    public ResponseEntity<?> markChapterAsCompleted(
            @RequestParam Long userId,
            @RequestParam Long chapterId) {
        try {
            UserProgressDTO progress = userProgressService.markChapterAsCompleted(userId, chapterId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/course/start")
    public ResponseEntity<?> startCourse(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        try {
            userProgressService.startCourse(userId, courseId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Curso iniciado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}