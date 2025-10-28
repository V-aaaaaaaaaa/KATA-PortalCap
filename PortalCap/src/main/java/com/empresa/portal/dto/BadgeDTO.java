package com.empresa.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {

    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private String badgeType;
    private LocalDateTime earnedAt;
}