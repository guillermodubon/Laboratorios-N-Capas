package com.server.app.repositories;

import com.server.app.entities.PlanPago;
import com.server.app.entities.enums.EstadoPlanPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanPagoRepository extends JpaRepository<PlanPago, Long> {
    Page<PlanPago> findByPrestamoIdOrderByNumeroCuota(Long prestamoId, Pageable pageable);

    List<PlanPago> findByPrestamoIdOrderByNumeroCuota(Long prestamoId);

    long countByPrestamoId(Long prestamoId);

    long countByPrestamoIdAndEstado(Long prestamoId, EstadoPlanPago estado);
}
