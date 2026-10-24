package com.autoscolombia.parqueadero.dto.celda;

import com.autoscolombia.parqueadero.model.enums.EstadoCelda;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CeldaEstadoUpdateDTO {

    @NotNull(message = "El nuevo estado de la celda es obligatorio")
    private EstadoCelda estado;
}
