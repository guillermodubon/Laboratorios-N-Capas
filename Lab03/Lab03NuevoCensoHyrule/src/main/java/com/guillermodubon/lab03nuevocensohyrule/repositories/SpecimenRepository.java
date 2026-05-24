package com.guillermodubon.lab03nuevocensohyrule.repositories;

import com.guillermodubon.lab03nuevocensohyrule.entities.Specimen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpecimenRepository extends JpaRepository<Specimen, UUID> {
}
