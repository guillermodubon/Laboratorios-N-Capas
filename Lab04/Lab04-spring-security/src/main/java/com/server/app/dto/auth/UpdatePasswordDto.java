package com.server.app.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordDto {
    @NotBlank(message = "La contrasena actual es obligatoria")
    private String oldpassword;

    @NotBlank(message = "La nueva contrasena es obligatoria")
    @Size(min = 8, max = 100, message = "La nueva contrasena debe tener entre 8 y 100 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&._-]).+$",
            message = "La nueva contrasena debe incluir mayuscula, minuscula, numero y caracter especial")
    private String newpassword;

    @NotBlank(message = "La confirmacion de contrasena es obligatoria")
    private String confirmpassword;
}
