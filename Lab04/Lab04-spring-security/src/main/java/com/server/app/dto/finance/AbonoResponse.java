package com.server.app.dto.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.app.entities.Abono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AbonoResponse(
        Long id,
        BigDecimal monto,
        @JsonProperty("fecha_pago") LocalDateTime fechaPago,
        @JsonProperty("recargo_mora") BigDecimal recargoMora,
        @JsonProperty("plan_pago_id") Long planPagoId
) {
    public static AbonoResponse from(Abono abono) {
        return new AbonoResponse(
                abono.getId(),
                abono.getMonto(),
                abono.getFechaPago(),
                abono.getRecargoMora(),
                abono.getPlanPago().getId()
        );
    }
}
