package com.server.app.entities;

import com.server.app.entities.enums.EstadoPrestamo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "prestamos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "capital_solicitado", nullable = false, precision = 19, scale = 2)
    private BigDecimal capitalSolicitado;

    @Column(name = "tasa_interes_anual", nullable = false, precision = 10, scale = 6)
    private BigDecimal tasaInteresAnual;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPrestamo estado = EstadoPrestamo.PENDIENTE;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;
}
