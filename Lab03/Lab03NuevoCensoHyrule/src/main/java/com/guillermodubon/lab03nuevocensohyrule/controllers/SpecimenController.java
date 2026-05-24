package com.guillermodubon.lab03nuevocensohyrule.controllers;

import com.guillermodubon.lab03nuevocensohyrule.dto.request.CreateSpecimenRequest;
import com.guillermodubon.lab03nuevocensohyrule.dto.request.UpdateSpecimenRequest;
import com.guillermodubon.lab03nuevocensohyrule.dto.response.GeneralResponse;
import com.guillermodubon.lab03nuevocensohyrule.dto.response.PageableResponse;
import com.guillermodubon.lab03nuevocensohyrule.dto.response.SpecimenResponse;
import com.guillermodubon.lab03nuevocensohyrule.services.SpecimenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/specimens")
@RequiredArgsConstructor
public class SpecimenController {
    private final SpecimenService specimenService;

    @PostMapping
    public ResponseEntity<GeneralResponse<SpecimenResponse>> createSpecimen(
            @Valid @RequestBody CreateSpecimenRequest request,
            HttpServletRequest servletRequest
    ) {
        SpecimenResponse specimen = specimenService.createSpecimen(request);
        return buildResponse("Specimen creado correctamente", HttpStatus.CREATED, specimen, servletRequest);
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<PageableResponse<SpecimenResponse>>> getAllSpecimens(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            HttpServletRequest servletRequest
    ) {
        PageableResponse<SpecimenResponse> specimens = specimenService.getAllSpecimens(page, size, sortBy, sortOrder);
        return buildResponse("Specimens cargados correctamente", HttpStatus.OK, specimens, servletRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<SpecimenResponse>> getSpecimenById(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        SpecimenResponse specimen = specimenService.getSpecimenById(id);
        return buildResponse("Specimen cargado correctamente", HttpStatus.OK, specimen, servletRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<SpecimenResponse>> updateSpecimen(
            @PathVariable UUID id,
            @RequestBody UpdateSpecimenRequest request,
            HttpServletRequest servletRequest
    ) {
        SpecimenResponse specimen = specimenService.updateSpecimen(id, request);
        return buildResponse("Specimen actualizado correctamente", HttpStatus.OK, specimen, servletRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<SpecimenResponse>> deleteSpecimen(
            @PathVariable UUID id,
            HttpServletRequest servletRequest
    ) {
        SpecimenResponse specimen = specimenService.deleteSpecimen(id);
        return buildResponse("Specimen eliminado correctamente", HttpStatus.OK, specimen, servletRequest);
    }

    private <T> ResponseEntity<GeneralResponse<T>> buildResponse(
            String message,
            HttpStatus status,
            T data,
            HttpServletRequest request
    ) {
        GeneralResponse<T> response = GeneralResponse.<T>builder()
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(getRequestUri(request))
                .data(data)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    private String getRequestUri(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return request.getRequestURI();
        }

        return request.getRequestURI() + "?" + queryString;
    }
}
