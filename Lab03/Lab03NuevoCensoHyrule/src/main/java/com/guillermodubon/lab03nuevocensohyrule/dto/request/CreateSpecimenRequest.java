package com.guillermodubon.lab03nuevocensohyrule.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpecimenRequest {
    @NotBlank(message = "El nombre del specimen no puede estar vacio.")
    private String name;

    @NotBlank(message = "La region de Hyrule debe ser especificada.")
    private String region;

    @NotNull(message = "Se requiere nivel de peligro.")
    private Integer dangerLevel;

    @NotNull(message = "Debes especificar si el specimen es amigable.")
    private Boolean isFriendly;
}
