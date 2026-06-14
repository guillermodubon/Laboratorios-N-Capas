package com.server.app.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {
    @NotBlank(message = "El nombre de usuario no puede estar vacio")
    private String username;

    @NotBlank(message = "La contrasena no puede estar vacia")
    private String password;
}
