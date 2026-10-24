package com.autoscolombia.parqueadero.dto.celda;

import com.autoscolombia.parqueadero.model.enums.EstadoCelda;
import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CeldaDTO {
    private Long id;
    private String numeroCelda;
    private TipoVehiculo tipoPermitido;
    private EstadoCelda estado;
    private Boolean activo;
    private String descripcion;
}
