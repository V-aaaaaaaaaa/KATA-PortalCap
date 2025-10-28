package com.empresa.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDTO {

    private Long userId;
    private Long courseId;
    private Long chapterId;
    private Boolean isCompleted;
    private String courseTitle;
    private Integer totalChapters;
    private Integer completedChapters;
    private Double progressPercentage;
}