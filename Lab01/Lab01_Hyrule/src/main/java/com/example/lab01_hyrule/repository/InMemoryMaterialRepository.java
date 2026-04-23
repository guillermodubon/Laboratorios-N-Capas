package com.example.lab01_hyrule.repository;

import com.example.lab01_hyrule.model.Material;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryMaterialRepository implements MaterialRepository {

    private final List<Material> materiales = List.of(
            Material.builder()
                    .nombre("Ámbar Rojo")
                    .categoria("Mineral")
                    .efecto("Defensa")
                    .precioVenta(30)
                    .ubicacionPrincipal("Cordillera de Hebra")
                    .rareza("Poco Común")
                    .build(),

            Material.builder()
                    .nombre("Ala de Keese")
                    .categoria("Parte de Monstruo")
                    .efecto("Sigilo")
                    .precioVenta(15)
                    .ubicacionPrincipal("Cueva de Hyrule")
                    .rareza("Común")
                    .build(),

            Material.builder()
                    .nombre("Pimienta Ardiente")
                    .categoria("Planta")
                    .efecto("Ataque")
                    .precioVenta(10)
                    .ubicacionPrincipal("Cordillera de Hebra")
                    .rareza("Común")
                    .build(),

            Material.builder()
                    .nombre("Trufa Vivaz")
                    .categoria("Comida")
                    .efecto("Corazones")
                    .precioVenta(20)
                    .ubicacionPrincipal("Bosque de Farone")
                    .rareza("Raro")
                    .build(),

            Material.builder()
                    .nombre("Flor Sigilosa")
                    .categoria("Planta")
                    .efecto("Sigilo")
                    .precioVenta(12)
                    .ubicacionPrincipal("Aldea Kakariko")
                    .rareza("Poco Común")
                    .build(),

            Material.builder()
                    .nombre("Diamante")
                    .categoria("Mineral")
                    .efecto("Defensa")
                    .precioVenta(500)
                    .ubicacionPrincipal("Volcán de Eldin")
                    .rareza("Legendario")
                    .build(),

            Material.builder()
                    .nombre("Cuerno de Dinraal")
                    .categoria("Parte de Monstruo")
                    .efecto("Ataque")
                    .precioVenta(300)
                    .ubicacionPrincipal("Cañón de Tanagar")
                    .rareza("Legendario")
                    .build(),

            Material.builder()
                    .nombre("Trufa Máxima")
                    .categoria("Comida")
                    .efecto("Corazones")
                    .precioVenta(100)
                    .ubicacionPrincipal("Bosque de Farone")
                    .rareza("Legendario")
                    .build()
    );

    @Override
    public List<Material> findAll() {
        return materiales;
    }
}
