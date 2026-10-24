package com.autoscolombia.parqueadero.dto.registro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroSalidaDTO {

    @NotNull(message = "El identificador del operador que liquida la salida es obligatorio")
    private Long idUsuarioSalida;

    @PositiveOrZero(message = "El total pagado no puede ser negativo")
    private BigDecimal totalPagado;

    private String observaciones;
}
