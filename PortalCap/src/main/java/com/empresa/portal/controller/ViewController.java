package com.empresa.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/course/{id}")
    public String course() {
        return "course";
    }

    @GetMapping("/capacitaciones")
    public String capacitaciones() {
        return "capacitaciones";
    }

    @GetMapping("/panelAdmin")
    public String panelAdmin() {
        return "panelAdmin";
    }

    @GetMapping("/courseForm")
    public String courseForm() {
        return "courseForm";
    }

    @GetMapping("/badge")
    public String badge() {
        return "badge";
    }

    @GetMapping("/module")
    public String module() {
        return "module";
    }
}