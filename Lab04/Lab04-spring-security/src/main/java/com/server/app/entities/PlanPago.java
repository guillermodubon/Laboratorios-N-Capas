package com.server.app.entities;

import com.server.app.entities.enums.EstadoPlanPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "planes_pago", uniqueConstraints = @UniqueConstraint(columnNames = {"prestamo_id", "numero_cuota"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "monto_capital", nullable = false, precision = 19, scale = 2)
    private BigDecimal montoCapital;

    @Column(name = "monto_interes", nullable = false, precision = 19, scale = 2)
    private BigDecimal montoInteres;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPlanPago estado = EstadoPlanPago.PENDIENTE;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;
}
