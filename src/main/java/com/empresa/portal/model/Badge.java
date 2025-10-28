package com.empresa.portal.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_id", unique = true, nullable = false)
    private Long moduleId;

    @Column(nullable = false)
    private String name;

    private String description;
    private String icon;
    private String color;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}