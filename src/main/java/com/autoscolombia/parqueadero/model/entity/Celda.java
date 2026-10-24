package com.autoscolombia.parqueadero.model.entity;

import com.autoscolombia.parqueadero.model.enums.EstadoCelda;
import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "celda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Celda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_celda", nullable = false, unique = true, length = 20)
    private String numeroCelda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_permitido", nullable = false, length = 20)
    private TipoVehiculo tipoPermitido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoCelda estado = EstadoCelda.DISPONIBLE;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoCelda.DISPONIBLE;
        }
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.numeroCelda != null) {
            this.numeroCelda = this.numeroCelda.trim().toUpperCase();
        }
    }
}
