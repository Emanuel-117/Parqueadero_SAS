package com.autoscolombia.parqueadero.dto.celda;

import com.autoscolombia.parqueadero.model.enums.EstadoCelda;
import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapaCeldaDTO {
    private Long idCelda;
    private String numeroCelda;
    private TipoVehiculo tipoPermitido;
    private EstadoCelda estado;
    private Boolean activo;
    private String colorEstado;      // "VERDE" (Disponible) o "ROJO" (Ocupada)
    private String colorHex;         // "#22C55E" o "#EF4444"
    private String descripcion;

    // Datos contextuales si la celda se encuentra OCUPADA
    private String placaVehiculoActual;
    private TipoVehiculo tipoVehiculoActual;
    private OffsetDateTime fechaIngresoVehiculo;
    private Long minutosTranscurridos;
    private String tiempoTranscurridoFormato;
}
