package com.empresa.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private Long moduleId;
    private String moduleName;
    private String title;
    private String description;
    private String instructor;
    private Integer durationHours;
    private String difficulty;
    private String thumbnailUrl;
    private Boolean isActive;
    private Integer totalChapters;
    private Integer completedChapters;
    private Double progressPercentage;
    private String videoFileName;
    private String videoUrl;
    private Integer videoDurationMinutes;
    private String pdfFileName;
    private String pdfUrl;
    private Integer pdfPages;
    private Boolean hasVideo = false;
    private Boolean hasPdf = false;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}