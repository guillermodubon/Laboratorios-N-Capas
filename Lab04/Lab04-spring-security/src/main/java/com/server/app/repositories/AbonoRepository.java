package com.server.app.repositories;

import com.server.app.entities.Abono;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface AbonoRepository extends JpaRepository<Abono, Long> {
    Page<Abono> findByPlanPagoIdOrderByFechaPagoDesc(Long planPagoId, Pageable pageable);

    @Query("select coalesce(sum(a.monto), 0) from Abono a where a.planPago.id = :planPagoId")
    BigDecimal sumMontoByPlanPagoId(@Param("planPagoId") Long planPagoId);

    @Query("select coalesce(sum(a.recargoMora), 0) from Abono a where a.planPago.id = :planPagoId")
    BigDecimal sumRecargoByPlanPagoId(@Param("planPagoId") Long planPagoId);
}
