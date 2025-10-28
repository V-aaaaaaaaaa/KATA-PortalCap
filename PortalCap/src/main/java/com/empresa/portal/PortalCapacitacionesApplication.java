package com.empresa.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PortalCapacitacionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalCapacitacionesApplication.class, args);
        System.out.println("===========================================");
        System.out.println(" Portal de Capacitaciones iniciado");
        System.out.println(" URL: http://localhost:8080");
        System.out.println(" MailHog: http://localhost:8025");
        System.out.println("===========================================");
    }
}