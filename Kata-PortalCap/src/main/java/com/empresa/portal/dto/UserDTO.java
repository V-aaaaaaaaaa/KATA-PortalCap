package com.empresa.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String role; // USER o ADMIN
    private Boolean accountLocked;
    private Integer failedLoginAttempts;
    private LocalDateTime createdAt;

    // No se incluye la contraseña por seguridad
}