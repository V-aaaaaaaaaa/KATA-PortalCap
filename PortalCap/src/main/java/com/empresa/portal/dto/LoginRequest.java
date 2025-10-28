package com.empresa.portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El correo es requerido")
    @Email(message = "Correo inválido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    private String password;
}