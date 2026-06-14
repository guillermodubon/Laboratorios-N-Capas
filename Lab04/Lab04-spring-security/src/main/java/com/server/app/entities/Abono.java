package com.server.app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "abonos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Abono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "recargo_mora", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal recargoMora = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "plan_pago_id", nullable = false)
    private PlanPago planPago;
}
