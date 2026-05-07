package com.guillermodubon.lab02_7_mares.service;

import com.guillermodubon.lab02_7_mares.entity.Pirate;
import com.guillermodubon.lab02_7_mares.repository.PirateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PirateService {

    private final PirateRepository pirateRepository;

    public PirateService(PirateRepository pirateRepository) {
        this.pirateRepository = pirateRepository;
    }

    public Pirate createPirate(Pirate pirate) {
        return pirateRepository.save(pirate);
    }

    public List<Pirate> getAllPirates() {
        return pirateRepository.findAll();
    }

    public Optional<Pirate> getPirateById(UUID id) {
        return pirateRepository.findById(id);
    }

    public Optional<Pirate> updatePirate(UUID id, Pirate pirateDetails) {
        return pirateRepository.findById(id)
                .map(existingPirate -> {
                    existingPirate.setName(pirateDetails.getName());
                    existingPirate.setBounty(pirateDetails.getBounty());
                    existingPirate.setCrew(pirateDetails.getCrew());
                    existingPirate.setIsAlive(pirateDetails.getIsAlive());

                    return pirateRepository.save(existingPirate);
                });
    }

    public boolean deletePirate(UUID id) {
        if (pirateRepository.existsById(id)) {
            pirateRepository.deleteById(id);
            return true;
        }

        return false;
    }
}