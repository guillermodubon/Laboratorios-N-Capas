package com.server.app.services;

import com.server.app.dto.finance.PlanPagoResponse;
import com.server.app.dto.finance.PrestamoCreateDto;
import com.server.app.dto.finance.PrestamoResponse;
import com.server.app.entities.PlanPago;
import com.server.app.entities.Prestamo;
import com.server.app.entities.User;
import com.server.app.entities.enums.EstadoPlanPago;
import com.server.app.entities.enums.EstadoPrestamo;
import com.server.app.exceptions.BadRequestException;
import com.server.app.exceptions.ForbiddenException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.PlanPagoRepository;
import com.server.app.repositories.PrestamoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrestamoService {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    private static final int MONEY_SCALE = 2;

    private final PrestamoRepository prestamoRepository;
    private final PlanPagoRepository planPagoRepository;
    private final UserService userService;

    public PrestamoService(PrestamoRepository prestamoRepository, PlanPagoRepository planPagoRepository,
                           UserService userService) {
        this.prestamoRepository = prestamoRepository;
        this.planPagoRepository = planPagoRepository;
        this.userService = userService;
    }

    @Transactional
    public PrestamoResponse create(User actor, PrestamoCreateDto dto) {
        User owner = resolveOwner(actor, dto.getUsuarioId());
        Prestamo prestamo = Prestamo.builder()
                .capitalSolicitado(money(dto.getCapitalSolicitado()))
                .tasaInteresAnual(dto.getTasaInteresAnual())
                .plazoMeses(dto.getPlazoMeses())
                .estado(EstadoPrestamo.PENDIENTE)
                .usuario(owner)
                .build();
        return PrestamoResponse.from(prestamoRepository.save(prestamo));
    }

    @Transactional(readOnly = true)
    public Page<PrestamoResponse> findAll(User actor, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Prestamo> result = isAdmin(actor)
                ? prestamoRepository.findAll(pageable)
                : prestamoRepository.findByUsuarioId(actor.getId(), pageable);
        return result.map(PrestamoResponse::from);
    }

    @Transactional(readOnly = true)
    public PrestamoResponse findById(User actor, Long id) {
        return PrestamoResponse.from(findAccessible(actor, id));
    }

    @Transactional
    public PrestamoResponse changeStatus(User actor, Long id, EstadoPrestamo status) {
        Prestamo prestamo = findAccessible(actor, id);
        if (prestamo.getEstado() == EstadoPrestamo.PAGADO && status != EstadoPrestamo.PAGADO) {
            throw new BadRequestException("Un prestamo PAGADO no puede cambiar de estado");
        }
        if (status == EstadoPrestamo.APROBADO && planPagoRepository.countByPrestamoId(id) == 0) {
            generatePaymentPlan(prestamo);
        }
        if (status == EstadoPrestamo.PAGADO) {
            long total = planPagoRepository.countByPrestamoId(id);
            long pending = planPagoRepository.countByPrestamoIdAndEstado(id, EstadoPlanPago.PENDIENTE);
            if (total == 0 || pending > 0) {
                throw new BadRequestException("El prestamo solo puede marcarse PAGADO cuando todas sus cuotas esten pagadas");
            }
        }
        if (status == EstadoPrestamo.PENDIENTE && planPagoRepository.countByPrestamoId(id) > 0) {
            throw new BadRequestException("No se puede regresar a PENDIENTE un prestamo con plan de pago");
        }
        prestamo.setEstado(status);
        return PrestamoResponse.from(prestamoRepository.save(prestamo));
    }

    @Transactional(readOnly = true)
    public Page<PlanPagoResponse> findPaymentPlan(User actor, Long prestamoId, int page, int size) {
        findAccessible(actor, prestamoId);
        return planPagoRepository.findByPrestamoIdOrderByNumeroCuota(prestamoId, PageRequest.of(page, size))
                .map(PlanPagoResponse::from);
    }

    @Transactional(readOnly = true)
    public Prestamo findAccessible(User actor, Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prestamo no encontrado"));
        if (!isAdmin(actor) && prestamo.getUsuario().getId() != actor.getId()) {
            throw new ForbiddenException("No puede acceder a prestamos de otro usuario");
        }
        return prestamo;
    }

    private void generatePaymentPlan(Prestamo prestamo) {
        BigDecimal balance = prestamo.getCapitalSolicitado();
        int months = prestamo.getPlazoMeses();
        BigDecimal monthlyRate = prestamo.getTasaInteresAnual()
                .divide(BigDecimal.valueOf(1200), MATH_CONTEXT);
        BigDecimal payment = calculateFixedPayment(balance, monthlyRate, months);
        List<PlanPago> plans = new ArrayList<>(months);

        for (int installment = 1; installment <= months; installment++) {
            BigDecimal interest = money(balance.multiply(monthlyRate, MATH_CONTEXT));
            BigDecimal principal = installment == months ? balance : money(payment.subtract(interest));
            if (principal.signum() <= 0) {
                throw new BadRequestException("La tasa y el plazo producen una cuota sin amortizacion de capital");
            }
            plans.add(PlanPago.builder()
                    .numeroCuota(installment)
                    .montoCapital(money(principal))
                    .montoInteres(interest)
                    .fechaVencimiento(LocalDate.now().plusMonths(installment))
                    .estado(EstadoPlanPago.PENDIENTE)
                    .prestamo(prestamo)
                    .build());
            balance = money(balance.subtract(principal));
        }
        planPagoRepository.saveAll(plans);
    }

    private BigDecimal calculateFixedPayment(BigDecimal capital, BigDecimal monthlyRate, int months) {
        if (monthlyRate.signum() == 0) {
            return money(capital.divide(BigDecimal.valueOf(months), MATH_CONTEXT));
        }
        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(months, MATH_CONTEXT);
        BigDecimal numerator = capital.multiply(monthlyRate, MATH_CONTEXT).multiply(factor, MATH_CONTEXT);
        return money(numerator.divide(factor.subtract(BigDecimal.ONE), MATH_CONTEXT));
    }

    private User resolveOwner(User actor, Integer requestedUserId) {
        if (requestedUserId == null || requestedUserId == actor.getId()) {
            return actor;
        }
        if (!isAdmin(actor)) {
            throw new ForbiddenException("Solo ADMIN puede crear prestamos para otro usuario");
        }
        return userService.findById(requestedUserId);
    }

    public boolean isAdmin(User user) {
        return user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().getName());
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
