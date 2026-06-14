package com.server.app.controllers;

import com.server.app.dto.finance.PlanPagoResponse;
import com.server.app.dto.finance.PrestamoCreateDto;
import com.server.app.dto.finance.PrestamoEstadoDto;
import com.server.app.dto.finance.PrestamoResponse;
import com.server.app.dto.response.Pagination;
import com.server.app.entities.User;
import com.server.app.services.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    public ResponseEntity<PrestamoResponse> create(@AuthenticationPrincipal User user,
                                                   @Valid @RequestBody PrestamoCreateDto dto) {
        return ResponseEntity.ok(prestamoService.create(user, dto));
    }

    @GetMapping
    public ResponseEntity<Pagination<PrestamoResponse>> findAll(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Pagination.from(prestamoService.findAll(user, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponse> findById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.findById(user, id));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<PrestamoResponse> changeStatus(@AuthenticationPrincipal User user, @PathVariable Long id,
                                                         @Valid @RequestBody PrestamoEstadoDto dto) {
        return ResponseEntity.ok(prestamoService.changeStatus(user, id, dto.getEstado()));
    }

    @GetMapping("/{id}/plan-pagos")
    public ResponseEntity<Pagination<PlanPagoResponse>> findPaymentPlan(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Pagination.from(prestamoService.findPaymentPlan(user, id, page, size)));
    }
}
