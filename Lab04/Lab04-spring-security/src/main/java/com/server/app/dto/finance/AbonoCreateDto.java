package com.server.app.dto.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AbonoCreateDto {
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
    private BigDecimal monto;

    @DecimalMin(value = "0.0", inclusive = true, message = "El recargo por mora no puede ser negativo")
    @JsonProperty("recargo_mora")
    private BigDecimal recargoMora;
}
