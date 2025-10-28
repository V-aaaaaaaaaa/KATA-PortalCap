package com.empresa.portal.service;

import com.empresa.portal.dto.ModuleWithCoursesDTO;
import com.empresa.portal.model.Module;
import com.empresa.portal.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseService courseService;

    // Obtener todos los módulos con sus cursos
    public List<ModuleWithCoursesDTO> getAllModulesWithCourses() {
        List<Module> modules = moduleRepository.findAll();

        return modules.stream()
                .map(this::convertToDTOWithCourses)
                .collect(Collectors.toList());
    }

    public ModuleWithCoursesDTO getModuleWithCourses(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));

        return convertToDTOWithCourses(module);
    }

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    public Optional<Module> getModuleById(Long id) {
        return moduleRepository.findById(id);
    }

    private ModuleWithCoursesDTO convertToDTOWithCourses(Module module) {
        ModuleWithCoursesDTO dto = new ModuleWithCoursesDTO();
        dto.setId(module.getId());
        dto.setName(module.getName());
        dto.setDescription(module.getDescription());
        dto.setIcon(module.getIcon());

        dto.setCourses(courseService.getCoursesByModule(module.getId()));

        return dto;
    }
}