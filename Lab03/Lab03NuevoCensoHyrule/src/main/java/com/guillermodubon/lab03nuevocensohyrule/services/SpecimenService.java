package com.guillermodubon.lab03nuevocensohyrule.services;

import com.guillermodubon.lab03nuevocensohyrule.dto.request.CreateSpecimenRequest;
import com.guillermodubon.lab03nuevocensohyrule.dto.request.UpdateSpecimenRequest;
import com.guillermodubon.lab03nuevocensohyrule.dto.response.PageableResponse;
import com.guillermodubon.lab03nuevocensohyrule.dto.response.SpecimenResponse;

import java.util.UUID;

public interface SpecimenService {
    SpecimenResponse createSpecimen(CreateSpecimenRequest request);

    PageableResponse<SpecimenResponse> getAllSpecimens(Integer page, Integer size, String sortBy, String sortOrder);

    SpecimenResponse getSpecimenById(UUID id);

    SpecimenResponse updateSpecimen(UUID id, UpdateSpecimenRequest request);

    SpecimenResponse deleteSpecimen(UUID id);
}
