package com.server.app.dto.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.app.entities.Prestamo;
import com.server.app.entities.enums.EstadoPrestamo;

import java.math.BigDecimal;

public record PrestamoResponse(
        Long id,
        @JsonProperty("capital_solicitado") BigDecimal capitalSolicitado,
        @JsonProperty("tasa_interes_anual") BigDecimal tasaInteresAnual,
        @JsonProperty("plazo_meses") Integer plazoMeses,
        EstadoPrestamo estado,
        @JsonProperty("usuario_id") Integer usuarioId
) {
    public static PrestamoResponse from(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getCapitalSolicitado(),
                prestamo.getTasaInteresAnual(),
                prestamo.getPlazoMeses(),
                prestamo.getEstado(),
                prestamo.getUsuario().getId()
        );
    }
}
