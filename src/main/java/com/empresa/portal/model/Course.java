package com.empresa.portal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 100)
    private String instructor;
    
    @Column(name = "duration_hours")
    private Integer durationHours;
    
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty = Difficulty.BEGINNER;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "video_file_name")
    private String videoFileName;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "video_duration_minutes")
    private Integer videoDurationMinutes;
    
    @Column(name = "pdf_file_name")
    private String pdfFileName;
    
    @Column(name = "pdf_url")
    private String pdfUrl;
    
    @Column(name = "pdf_pages")
    private Integer pdfPages;
    
    @Column(name = "has_video")
    private Boolean hasVideo = false;
    
    @Column(name = "has_pdf")
    private Boolean hasPdf = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Chapter> chapters = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum Difficulty {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}