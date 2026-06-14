package com.server.app.services;

import com.server.app.dto.finance.AbonoCreateDto;
import com.server.app.dto.finance.AbonoResponse;
import com.server.app.entities.Abono;
import com.server.app.entities.PlanPago;
import com.server.app.entities.Prestamo;
import com.server.app.entities.User;
import com.server.app.entities.enums.EstadoPlanPago;
import com.server.app.entities.enums.EstadoPrestamo;
import com.server.app.exceptions.BadRequestException;
import com.server.app.exceptions.ForbiddenException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.AbonoRepository;
import com.server.app.repositories.PlanPagoRepository;
import com.server.app.repositories.PrestamoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class AbonoService {

    private final AbonoRepository abonoRepository;
    private final PlanPagoRepository planPagoRepository;
    private final PrestamoRepository prestamoRepository;

    public AbonoService(AbonoRepository abonoRepository, PlanPagoRepository planPagoRepository,
                        PrestamoRepository prestamoRepository) {
        this.abonoRepository = abonoRepository;
        this.planPagoRepository = planPagoRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @Transactional
    public AbonoResponse create(User actor, Long planPagoId, AbonoCreateDto dto) {
        PlanPago plan = findAccessible(actor, planPagoId);
        if (plan.getPrestamo().getEstado() != EstadoPrestamo.APROBADO) {
            throw new BadRequestException("Solo se pueden registrar abonos para prestamos APROBADOS");
        }
        if (plan.getEstado() == EstadoPlanPago.PAGADO) {
            throw new BadRequestException("La cuota ya esta pagada");
        }

        BigDecimal previousPayments = money(abonoRepository.sumMontoByPlanPagoId(planPagoId));
        BigDecimal previousLateFees = money(abonoRepository.sumRecargoByPlanPagoId(planPagoId));
        BigDecimal lateFee = dto.getRecargoMora() == null ? BigDecimal.ZERO : money(dto.getRecargoMora());
        BigDecimal amount = money(dto.getMonto());
        BigDecimal totalDue = money(plan.getMontoCapital().add(plan.getMontoInteres())
                .add(previousLateFees).add(lateFee));
        BigDecimal outstanding = money(totalDue.subtract(previousPayments));

        if (amount.compareTo(outstanding) > 0) {
            throw new BadRequestException("El abono supera el saldo pendiente de la cuota: " + outstanding);
        }

        Abono abono = Abono.builder()
                .monto(amount)
                .fechaPago(LocalDateTime.now())
                .recargoMora(lateFee)
                .planPago(plan)
                .build();
        abono = abonoRepository.save(abono);

        if (previousPayments.add(amount).compareTo(totalDue) >= 0) {
            plan.setEstado(EstadoPlanPago.PAGADO);
            planPagoRepository.save(plan);
            updateLoanIfPaid(plan.getPrestamo());
        }
        return AbonoResponse.from(abono);
    }

    @Transactional(readOnly = true)
    public Page<AbonoResponse> findAll(User actor, Long planPagoId, int page, int size) {
        findAccessible(actor, planPagoId);
        return abonoRepository.findByPlanPagoIdOrderByFechaPagoDesc(planPagoId, PageRequest.of(page, size))
                .map(AbonoResponse::from);
    }

    private PlanPago findAccessible(User actor, Long id) {
        PlanPago plan = planPagoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Plan de pago no encontrado"));
        boolean admin = actor.getRole() != null && "ADMIN".equalsIgnoreCase(actor.getRole().getName());
        if (!admin && plan.getPrestamo().getUsuario().getId() != actor.getId()) {
            throw new ForbiddenException("No puede acceder a planes de pago de otro usuario");
        }
        return plan;
    }

    private void updateLoanIfPaid(Prestamo prestamo) {
        long pending = planPagoRepository.countByPrestamoIdAndEstado(prestamo.getId(), EstadoPlanPago.PENDIENTE);
        if (pending == 0) {
            prestamo.setEstado(EstadoPrestamo.PAGADO);
            prestamoRepository.save(prestamo);
        }
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
