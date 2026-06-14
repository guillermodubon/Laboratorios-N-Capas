package com.server.app.dto.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrestamoCreateDto {

    @NotNull(message = "El capital solicitado es obligatorio")
    @DecimalMin(value = "0.01", message = "El capital solicitado debe ser mayor que cero")
    @JsonProperty("capital_solicitado")
    private BigDecimal capitalSolicitado;

    @NotNull(message = "La tasa de interes anual es obligatoria")
    @DecimalMin(value = "0.0", inclusive = true, message = "La tasa de interes anual no puede ser negativa")
    @JsonProperty("tasa_interes_anual")
    private BigDecimal tasaInteresAnual;

    @NotNull(message = "El plazo en meses es obligatorio")
    @Min(value = 1, message = "El plazo debe ser de al menos un mes")
    @Max(value = 600, message = "El plazo no puede superar 600 meses")
    @JsonProperty("plazo_meses")
    private Integer plazoMeses;

    @JsonProperty("usuario_id")
    private Integer usuarioId;
}
