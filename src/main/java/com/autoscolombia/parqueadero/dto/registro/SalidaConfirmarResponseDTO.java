package com.autoscolombia.parqueadero.dto.registro;

import com.autoscolombia.parqueadero.dto.novedad.NovedadDTO;
import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalidaConfirmarResponseDTO {
    private Long idRegistro;
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private Long idCelda;
    private String numeroCelda;
    private OffsetDateTime fechaIngreso;
    private OffsetDateTime fechaSalida;
    private long horasTranscurridas;
    private long minutosTranscurridos;
    private String tiempoTranscurridoFormato;
    private BigDecimal valorTarifaHora;
    private BigDecimal totalCobrado;
    private String operadorSalida;
    private String observaciones;
    private List<NovedadDTO> historialNovedades;
    private String mensaje;
}
