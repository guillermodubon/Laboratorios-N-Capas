package com.server.app.dto.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.app.entities.PlanPago;
import com.server.app.entities.enums.EstadoPlanPago;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanPagoResponse(
        Long id,
        @JsonProperty("numero_cuota") Integer numeroCuota,
        @JsonProperty("monto_capital") BigDecimal montoCapital,
        @JsonProperty("monto_interes") BigDecimal montoInteres,
        @JsonProperty("monto_cuota") BigDecimal montoCuota,
        @JsonProperty("fecha_vencimiento") LocalDate fechaVencimiento,
        EstadoPlanPago estado,
        @JsonProperty("prestamo_id") Long prestamoId
) {
    public static PlanPagoResponse from(PlanPago plan) {
        return new PlanPagoResponse(
                plan.getId(),
                plan.getNumeroCuota(),
                plan.getMontoCapital(),
                plan.getMontoInteres(),
                plan.getMontoCapital().add(plan.getMontoInteres()),
                plan.getFechaVencimiento(),
                plan.getEstado(),
                plan.getPrestamo().getId()
        );
    }
}
