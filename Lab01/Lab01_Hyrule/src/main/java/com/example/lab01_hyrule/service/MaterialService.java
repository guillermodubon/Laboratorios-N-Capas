package com.example.lab01_hyrule.service;

import com.example.lab01_hyrule.model.Material;
import com.example.lab01_hyrule.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public List<Material> obtenerTodosOrdenadosPorPrecioDesc() {
        return materialRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Material::getPrecioVenta).reversed())
                .toList();
    }

    public Optional<Material> obtenerMaterialMasCaro() {
        return materialRepository.findAll()
                .stream()
                .max(Comparator.comparingInt(Material::getPrecioVenta));
    }

    public List<Material> obtenerLegendarios() {
        return materialRepository.findAll()
                .stream()
                .filter(material -> material.getRareza().equalsIgnoreCase("Legendario"))
                .toList();
    }

    public List<String> obtenerUbicacionesSinRepetir() {
        return materialRepository.findAll()
                .stream()
                .map(Material::getUbicacionPrincipal)
                .distinct()
                .toList();
    }
}
