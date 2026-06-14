package com.server.app.dto.finance;

import com.server.app.entities.enums.EstadoPrestamo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrestamoEstadoDto {
    @NotNull(message = "El estado es obligatorio")
    private EstadoPrestamo estado;
}
