package com.server.app.services;

import com.server.app.entities.PlanPago;
import com.server.app.entities.Prestamo;
import com.server.app.entities.Role;
import com.server.app.entities.User;
import com.server.app.entities.enums.EstadoPlanPago;
import com.server.app.entities.enums.EstadoPrestamo;
import com.server.app.repositories.PlanPagoRepository;
import com.server.app.repositories.PrestamoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PrestamoServiceTests {

    @Test
    void approvingLoanGeneratesFixedPaymentAmortizationPlan() {
        PrestamoRepository prestamoRepository = mock(PrestamoRepository.class);
        PlanPagoRepository planPagoRepository = mock(PlanPagoRepository.class);
        UserService userService = mock(UserService.class);
        PrestamoService service = new PrestamoService(prestamoRepository, planPagoRepository, userService);

        User admin = User.builder()
                .id(1)
                .role(Role.builder().name("ADMIN").active(true).permissions(Set.of()).build())
                .build();
        Prestamo loan = Prestamo.builder()
                .id(10L)
                .capitalSolicitado(new BigDecimal("1200.00"))
                .tasaInteresAnual(new BigDecimal("12.00"))
                .plazoMeses(12)
                .estado(EstadoPrestamo.PENDIENTE)
                .usuario(admin)
                .build();

        when(prestamoRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(planPagoRepository.countByPrestamoId(10L)).thenReturn(0L);
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.changeStatus(admin, 10L, EstadoPrestamo.APROBADO);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<PlanPago>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(planPagoRepository).saveAll(captor.capture());
        List<PlanPago> plans = StreamSupport.stream(captor.getValue().spliterator(), false).toList();

        assertThat(plans).hasSize(12);
        assertThat(plans).allMatch(plan -> plan.getEstado() == EstadoPlanPago.PENDIENTE);
        assertThat(plans.stream().map(PlanPago::getMontoCapital).reduce(BigDecimal.ZERO, BigDecimal::add))
                .isEqualByComparingTo("1200.00");
        assertThat(loan.getEstado()).isEqualTo(EstadoPrestamo.APROBADO);
    }
}
