package com.autoscolombia.parqueadero.dto.registro;

import com.autoscolombia.parqueadero.dto.celda.CeldaDTO;
import com.autoscolombia.parqueadero.dto.usuario.UsuarioDTO;
import com.autoscolombia.parqueadero.dto.vehiculo.VehiculoDTO;
import com.autoscolombia.parqueadero.model.enums.EstadoRegistro;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroIngresoResponseDTO {
    private Long id;
    private VehiculoDTO vehiculo;
    private CeldaDTO celda;
    private UsuarioDTO operadorIngreso;
    private UsuarioDTO operadorSalida;
    private OffsetDateTime fechaIngreso;
    private OffsetDateTime fechaSalida;
    private EstadoRegistro estado;
    private BigDecimal valorTarifaHora;
    private BigDecimal totalPagado;
    private String observaciones;
}
