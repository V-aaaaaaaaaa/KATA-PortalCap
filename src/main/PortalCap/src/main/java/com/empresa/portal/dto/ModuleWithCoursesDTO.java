package com.empresa.portal.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleWithCoursesDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private List<CourseDTO> courses = new ArrayList<>();
}