package com.example.lab01_hyrule.runner;

import com.example.lab01_hyrule.model.Material;
import com.example.lab01_hyrule.service.MaterialService;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MaterialRunner implements CommandLineRunner {

    private final MaterialService materialService;

    public MaterialRunner(MaterialService materialService) {
        this.materialService = materialService;
    }

    @Override
    public void run(String @NonNull ... args) {
        System.out.println("-CATALOGO COMPLETO ORDENADO DE MAYOR A MENOR PRECIO ===");
        imprimirMateriales(materialService.obtenerTodosOrdenadosPorPrecioDesc());

        System.out.println("\n=== MATERIAL MAS CARO ===");
        materialService.obtenerMaterialMasCaro()
                .ifPresent(this::imprimirMaterial);

        System.out.println("\n=== MATERIALES LEGENDARIOS ===");
        imprimirMateriales(materialService.obtenerLegendarios());

        System.out.println("\n=== UBICACIONES REGISTRADAS SIN REPETIR ===");
        materialService.obtenerUbicacionesSinRepetir()
                .forEach(ubicacion ->
                        System.out.println("Ubicacion: " + ubicacion));
    }

    private void imprimirMateriales(List<Material> materiales) {
        materiales.forEach(this::imprimirMaterial);
    }

    private void imprimirMaterial(Material material) {
        System.out.println("Nombre: " + material.getNombre()
                + " | Categoria: " + material.getCategoria()
                + " | Precio: " + material.getPrecioVenta() + " Rupias");
    }
}
