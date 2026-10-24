package com.autoscolombia.parqueadero.dto.vehiculo;

import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculoDTO {
    private Long id;
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private String marca;
    private String modelo;
    private String color;
    private OffsetDateTime fechaRegistro;
}
