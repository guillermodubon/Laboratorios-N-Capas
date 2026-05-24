package com.guillermodubon.lab03nuevocensohyrule.services.impl;

import com.guillermodubon.lab03nuevocensohyrule.dto.request.CreateSpecimenRequest;
import com.guillermodubon.lab03nuevocensohyrule.dto.request.UpdateSpecimenRequest;
import com.guillermodubon.lab03nuevocensohyrule.dto.response.PageableResponse;
import com.guillermodubon.lab03nuevocensohyrule.dto.response.SpecimenResponse;
import com.guillermodubon.lab03nuevocensohyrule.entities.Specimen;
import com.guillermodubon.lab03nuevocensohyrule.exceptions.ResourceNotFoundException;
import com.guillermodubon.lab03nuevocensohyrule.mappers.SpecimenMapper;
import com.guillermodubon.lab03nuevocensohyrule.repositories.SpecimenRepository;
import com.guillermodubon.lab03nuevocensohyrule.services.SpecimenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpecimenServiceImpl implements SpecimenService {
    private final SpecimenRepository specimenRepository;
    private final SpecimenMapper specimenMapper;

    @Override
    @Transactional
    public SpecimenResponse createSpecimen(CreateSpecimenRequest request) {
        return specimenMapper.toDto(
                specimenRepository.save(specimenMapper.toEntityCreate(request))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<SpecimenResponse> getAllSpecimens(
            Integer page,
            Integer size,
            String sortBy,
            String sortOrder
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Specimen> specimens = specimenRepository.findAll(pageable);

        if (specimens.isEmpty()) {
            throw new ResourceNotFoundException("No specimens are registered in Hyrule");
        }

        Page<SpecimenResponse> specimenResponses = specimenMapper.toDtoPage(specimens);

        return PageableResponse.<SpecimenResponse>builder()
                .content(specimenResponses.getContent())
                .pageNumber(specimenResponses.getNumber())
                .pageSize(specimenResponses.getSize())
                .totalElements(specimenResponses.getTotalElements())
                .totalPages(specimenResponses.getTotalPages())
                .first(specimenResponses.isFirst())
                .last(specimenResponses.isLast())
                .empty(specimenResponses.isEmpty())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SpecimenResponse getSpecimenById(UUID id) {
        return specimenMapper.toDto(findSpecimenById(id));
    }

    @Override
    @Transactional
    public SpecimenResponse updateSpecimen(UUID id, UpdateSpecimenRequest request) {
        Specimen currentSpecimen = findSpecimenById(id);
        Specimen updatedSpecimen = specimenMapper.toEntityUpdate(request, id, currentSpecimen);
        return specimenMapper.toDto(specimenRepository.save(updatedSpecimen));
    }

    @Override
    @Transactional
    public SpecimenResponse deleteSpecimen(UUID id) {
        Specimen existingSpecimen = findSpecimenById(id);
        specimenRepository.deleteById(id);
        return specimenMapper.toDto(existingSpecimen);
    }

    private Specimen findSpecimenById(UUID id) {
        return specimenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El Specimen no fue encontrado"));
    }
}
