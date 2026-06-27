package com.guillermodubon.lab02_7_mares.service;

import com.guillermodubon.lab02_7_mares.entity.Pirate;
import com.guillermodubon.lab02_7_mares.repository.PirateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PirateServiceTest {

    @Mock
    private PirateRepository pirateRepository;

    @InjectMocks
    private PirateService pirateService;

    @Test
    void createPirate_debeGuardarPirataEnRepositorio() {
        Pirate pirateToSave = new Pirate(
                null,
                "Monkey D. Luffy",
                3000000000.0,
                "Sombrero de Paja",
                true
        );

        Pirate savedPirate = new Pirate(
                UUID.randomUUID(),
                "Monkey D. Luffy",
                3000000000.0,
                "Sombrero de Paja",
                true
        );

        when(pirateRepository.save(pirateToSave)).thenReturn(savedPirate);

        Pirate result = pirateService.createPirate(pirateToSave);

        assertNotNull(result.getId());
        assertEquals("Monkey D. Luffy", result.getName());
        assertEquals(3000000000.0, result.getBounty());
        assertEquals("Sombrero de Paja", result.getCrew());
        assertTrue(result.getIsAlive());

        verify(pirateRepository, times(1)).save(pirateToSave);
    }

    @Test
    void getAllPirates_debeRetornarListaDePiratas() {
        Pirate pirate1 = new Pirate(
                UUID.randomUUID(),
                "Roronoa Zoro",
                1111000000.0,
                "Sombrero de Paja",
                true
        );

        Pirate pirate2 = new Pirate(
                UUID.randomUUID(),
                "Buggy",
                3189000000.0,
                "Cross Guild",
                true
        );

        when(pirateRepository.findAll()).thenReturn(List.of(pirate1, pirate2));

        List<Pirate> result = pirateService.getAllPirates();

        assertEquals(2, result.size());
        assertEquals("Roronoa Zoro", result.get(0).getName());
        assertEquals("Buggy", result.get(1).getName());

        verify(pirateRepository, times(1)).findAll();
    }

    @Test
    void getPirateById_cuandoExiste_debeRetornarPirata() {
        UUID pirateId = UUID.randomUUID();

        Pirate pirate = new Pirate(
                pirateId,
                "Trafalgar Law",
                3000000000.0,
                "Heart Pirates",
                true
        );

        when(pirateRepository.findById(pirateId)).thenReturn(Optional.of(pirate));

        Optional<Pirate> result = pirateService.getPirateById(pirateId);

        assertTrue(result.isPresent());
        assertEquals(pirateId, result.get().getId());
        assertEquals("Trafalgar Law", result.get().getName());

        verify(pirateRepository, times(1)).findById(pirateId);
    }

    @Test
    void getPirateById_cuandoNoExiste_debeRetornarOptionalVacio() {
        UUID pirateId = UUID.randomUUID();

        when(pirateRepository.findById(pirateId)).thenReturn(Optional.empty());

        Optional<Pirate> result = pirateService.getPirateById(pirateId);

        assertTrue(result.isEmpty());

        verify(pirateRepository, times(1)).findById(pirateId);
    }

    @Test
    void updatePirate_cuandoExiste_debeActualizarCamposYGuardar() {
        UUID pirateId = UUID.randomUUID();

        Pirate existingPirate = new Pirate(
                pirateId,
                "Sanji",
                1032000000.0,
                "Sombrero de Paja",
                true
        );

        Pirate pirateDetails = new Pirate(
                null,
                "Vinsmoke Sanji",
                1032000000.0,
                "Sombrero de Paja",
                true
        );

        Pirate updatedPirate = new Pirate(
                pirateId,
                "Vinsmoke Sanji",
                1032000000.0,
                "Sombrero de Paja",
                true
        );

        when(pirateRepository.findById(pirateId)).thenReturn(Optional.of(existingPirate));
        when(pirateRepository.save(existingPirate)).thenReturn(updatedPirate);

        Optional<Pirate> result = pirateService.updatePirate(pirateId, pirateDetails);

        assertTrue(result.isPresent());
        assertEquals("Vinsmoke Sanji", result.get().getName());
        assertEquals(1032000000.0, result.get().getBounty());
        assertEquals("Sombrero de Paja", result.get().getCrew());
        assertTrue(result.get().getIsAlive());

        ArgumentCaptor<Pirate> pirateCaptor = ArgumentCaptor.forClass(Pirate.class);
        verify(pirateRepository, times(1)).findById(pirateId);
        verify(pirateRepository, times(1)).save(pirateCaptor.capture());

        Pirate capturedPirate = pirateCaptor.getValue();
        assertEquals("Vinsmoke Sanji", capturedPirate.getName());
        assertEquals(1032000000.0, capturedPirate.getBounty());
    }

    @Test
    void updatePirate_cuandoNoExiste_noDebeGuardar() {
        UUID pirateId = UUID.randomUUID();

        Pirate pirateDetails = new Pirate(
                null,
                "Nico Robin",
                930000000.0,
                "Sombrero de Paja",
                true
        );

        when(pirateRepository.findById(pirateId)).thenReturn(Optional.empty());

        Optional<Pirate> result = pirateService.updatePirate(pirateId, pirateDetails);

        assertTrue(result.isEmpty());

        verify(pirateRepository, times(1)).findById(pirateId);
        verify(pirateRepository, never()).save(any(Pirate.class));
    }

    @Test
    void deletePirate_cuandoExiste_debeEliminarYRetornarTrue() {
        UUID pirateId = UUID.randomUUID();

        when(pirateRepository.existsById(pirateId)).thenReturn(true);
        doNothing().when(pirateRepository).deleteById(pirateId);

        boolean result = pirateService.deletePirate(pirateId);

        assertTrue(result);

        verify(pirateRepository, times(1)).existsById(pirateId);
        verify(pirateRepository, times(1)).deleteById(pirateId);
    }

    @Test
    void deletePirate_cuandoNoExiste_debeRetornarFalseYNoEliminar() {
        UUID pirateId = UUID.randomUUID();

        when(pirateRepository.existsById(pirateId)).thenReturn(false);

        boolean result = pirateService.deletePirate(pirateId);

        assertFalse(result);

        verify(pirateRepository, times(1)).existsById(pirateId);
        verify(pirateRepository, never()).deleteById(any(UUID.class));
    }
}
