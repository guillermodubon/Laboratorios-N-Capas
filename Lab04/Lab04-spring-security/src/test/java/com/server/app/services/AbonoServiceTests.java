package com.server.app.services;

import com.server.app.dto.finance.AbonoCreateDto;
import com.server.app.entities.PlanPago;
import com.server.app.entities.Prestamo;
import com.server.app.entities.User;
import com.server.app.entities.enums.EstadoPlanPago;
import com.server.app.entities.enums.EstadoPrestamo;
import com.server.app.repositories.AbonoRepository;
import com.server.app.repositories.PlanPagoRepository;
import com.server.app.repositories.PrestamoRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AbonoServiceTests {

    @Test
    void fullInstallmentPaymentClosesInstallmentAndLoan() {
        AbonoRepository abonoRepository = mock(AbonoRepository.class);
        PlanPagoRepository planPagoRepository = mock(PlanPagoRepository.class);
        PrestamoRepository prestamoRepository = mock(PrestamoRepository.class);
        AbonoService service = new AbonoService(abonoRepository, planPagoRepository, prestamoRepository);

        User owner = User.builder().id(2).build();
        Prestamo loan = Prestamo.builder().id(20L).usuario(owner).estado(EstadoPrestamo.APROBADO).build();
        PlanPago plan = PlanPago.builder()
                .id(30L)
                .prestamo(loan)
                .montoCapital(new BigDecimal("90.00"))
                .montoInteres(new BigDecimal("10.00"))
                .estado(EstadoPlanPago.PENDIENTE)
                .build();
        AbonoCreateDto dto = new AbonoCreateDto();
        dto.setMonto(new BigDecimal("100.00"));

        when(planPagoRepository.findById(30L)).thenReturn(Optional.of(plan));
        when(abonoRepository.sumMontoByPlanPagoId(30L)).thenReturn(BigDecimal.ZERO);
        when(abonoRepository.sumRecargoByPlanPagoId(30L)).thenReturn(BigDecimal.ZERO);
        when(abonoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(planPagoRepository.countByPrestamoIdAndEstado(20L, EstadoPlanPago.PENDIENTE)).thenReturn(0L);

        service.create(owner, 30L, dto);

        assertThat(plan.getEstado()).isEqualTo(EstadoPlanPago.PAGADO);
        assertThat(loan.getEstado()).isEqualTo(EstadoPrestamo.PAGADO);
        verify(planPagoRepository).save(plan);
        verify(prestamoRepository).save(loan);
    }
}
