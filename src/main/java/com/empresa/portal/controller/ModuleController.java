package com.empresa.portal.controller;

import com.empresa.portal.dto.ModuleWithCoursesDTO;
import com.empresa.portal.model.Module;
import com.empresa.portal.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "*")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping("/with-courses")
    public ResponseEntity<List<ModuleWithCoursesDTO>> getAllModulesWithCourses() {
        List<ModuleWithCoursesDTO> modules = moduleService.getAllModulesWithCourses();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/{id}/with-courses")
    public ResponseEntity<ModuleWithCoursesDTO> getModuleWithCourses(@PathVariable Long id) {
        ModuleWithCoursesDTO module = moduleService.getModuleWithCourses(id);
        return ResponseEntity.ok(module);
    }

    @GetMapping
    public ResponseEntity<List<Module>> getAllModules() {
        List<Module> modules = moduleService.getAllModules();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Module> getModuleById(@PathVariable Long id) {
        return moduleService.getModuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}