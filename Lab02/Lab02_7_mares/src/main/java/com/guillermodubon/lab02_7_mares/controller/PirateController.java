package com.guillermodubon.lab02_7_mares.controller;

import com.guillermodubon.lab02_7_mares.entity.Pirate;
import com.guillermodubon.lab02_7_mares.service.PirateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pirates")
public class PirateController {

    private final PirateService pirateService;

    public PirateController(PirateService pirateService) {
        this.pirateService = pirateService;
    }

    @PostMapping
    public ResponseEntity<Pirate> createPirate(@RequestBody Pirate pirate) {
        Pirate createdPirate = pirateService.createPirate(pirate);
        return ResponseEntity.ok(createdPirate);
    }

    @GetMapping
    public ResponseEntity<List<Pirate>> getAllPirates() {
        List<Pirate> pirates = pirateService.getAllPirates();
        return ResponseEntity.ok(pirates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pirate> getPirateById(@PathVariable UUID id) {
        return pirateService.getPirateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pirate> updatePirate(@PathVariable UUID id, @RequestBody Pirate pirateDetails) {
        return pirateService.updatePirate(id, pirateDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePirate(@PathVariable UUID id) {
        boolean deleted = pirateService.deletePirate(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
