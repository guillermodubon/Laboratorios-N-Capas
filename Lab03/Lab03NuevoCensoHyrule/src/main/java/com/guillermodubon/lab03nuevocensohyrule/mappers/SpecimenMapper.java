package com.guillermodubon.lab03nuevocensohyrule.mappers;

import com.guillermodubon.lab03nuevocensohyrule.dto.request.CreateSpecimenRequest;
import com.guillermodubon.lab03nuevocensohyrule.dto.request.UpdateSpecimenRequest;
import com.guillermodubon.lab03nuevocensohyrule.dto.response.SpecimenResponse;
import com.guillermodubon.lab03nuevocensohyrule.entities.Specimen;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class SpecimenMapper {

    public Specimen toEntityCreate(CreateSpecimenRequest request) {
        return Specimen.builder()
                .name(request.getName())
                .region(request.getRegion())
                .dangerLevel(request.getDangerLevel())
                .isFriendly(request.getIsFriendly())
                .build();
    }

    public Specimen toEntityUpdate(UpdateSpecimenRequest request, UUID id, Specimen currentSpecimen) {
        return Specimen.builder()
                .id(id)
                .name(Objects.requireNonNullElse(request.getName(), currentSpecimen.getName()))
                .region(Objects.requireNonNullElse(request.getRegion(), currentSpecimen.getRegion()))
                .dangerLevel(Objects.requireNonNullElse(request.getDangerLevel(), currentSpecimen.getDangerLevel()))
                .isFriendly(Objects.requireNonNullElse(request.getIsFriendly(), currentSpecimen.getIsFriendly()))
                .build();
    }

    public Specimen toEntityUpdate(UpdateSpecimenRequest request, UUID id) {
        return Specimen.builder()
                .id(id)
                .name(request.getName())
                .region(request.getRegion())
                .dangerLevel(request.getDangerLevel())
                .isFriendly(request.getIsFriendly())
                .build();
    }

    public SpecimenResponse toDto(Specimen specimen) {
        return SpecimenResponse.builder()
                .id(specimen.getId())
                .name(specimen.getName())
                .region(specimen.getRegion())
                .dangerLevel(specimen.getDangerLevel())
                .isFriendly(specimen.getIsFriendly())
                .build();
    }

    public Page<SpecimenResponse> toDtoPage(Page<Specimen> specimens) {
        return specimens.map(this::toDto);
    }
}
