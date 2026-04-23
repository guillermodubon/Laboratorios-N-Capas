package com.example.lab01_hyrule.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Material {

    private final String nombre;
    private final String categoria;
    private final String efecto;
    private final int precioVenta;
    private final String ubicacionPrincipal;
    private final String rareza;
}