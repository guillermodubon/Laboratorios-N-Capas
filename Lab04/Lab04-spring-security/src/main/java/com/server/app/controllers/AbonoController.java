package com.server.app.controllers;

import com.server.app.dto.finance.AbonoCreateDto;
import com.server.app.dto.finance.AbonoResponse;
import com.server.app.dto.response.Pagination;
import com.server.app.entities.User;
import com.server.app.services.AbonoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planes-pago/{planPagoId}/abonos")
public class AbonoController {

    private final AbonoService abonoService;

    public AbonoController(AbonoService abonoService) {
        this.abonoService = abonoService;
    }

    @PostMapping
    public ResponseEntity<AbonoResponse> create(@AuthenticationPrincipal User user,
                                                @PathVariable Long planPagoId,
                                                @Valid @RequestBody AbonoCreateDto dto) {
        return ResponseEntity.ok(abonoService.create(user, planPagoId, dto));
    }

    @GetMapping
    public ResponseEntity<Pagination<AbonoResponse>> findAll(
            @AuthenticationPrincipal User user,
            @PathVariable Long planPagoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Pagination.from(abonoService.findAll(user, planPagoId, page, size)));
    }
}
