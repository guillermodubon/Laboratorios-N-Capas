package com.guillermodubon.lab02_7_mares.repository;

import com.guillermodubon.lab02_7_mares.entity.Pirate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PirateRepository extends JpaRepository<Pirate, UUID> {
}
