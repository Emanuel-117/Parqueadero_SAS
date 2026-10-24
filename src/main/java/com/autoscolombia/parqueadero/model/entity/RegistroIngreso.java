package com.autoscolombia.parqueadero.model.entity;

import com.autoscolombia.parqueadero.model.enums.EstadoRegistro;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registro_ingreso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_celda", nullable = false)
    private Celda celda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_ingreso", nullable = false)
    private Usuario usuarioIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_salida")
    private Usuario usuarioSalida;

    @Column(name = "fecha_ingreso", nullable = false, updatable = false)
    private OffsetDateTime fechaIngreso;

    @Column(name = "fecha_salida")
    private OffsetDateTime fechaSalida;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoRegistro estado = EstadoRegistro.ACTIVO;

    @Column(name = "valor_tarifa_hora", precision = 10, scale = 2)
    private BigDecimal valorTarifaHora;

    @Column(name = "total_pagado", precision = 10, scale = 2)
    private BigDecimal totalPagado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @OneToMany(mappedBy = "registroIngreso", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Novedad> novedades = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.fechaIngreso == null) {
            this.fechaIngreso = OffsetDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoRegistro.ACTIVO;
        }
    }
}
