package com.autoscolombia.parqueadero.dto.registro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroIngresoCreateDTO {

    @NotBlank(message = "La placa del vehículo es obligatoria")
    private String placa;

    @NotNull(message = "El identificador de la celda es obligatorio")
    private Long idCelda;

    @NotNull(message = "El identificador del usuario/operador de ingreso es obligatorio")
    private Long idUsuarioIngreso;

    @Positive(message = "La tarifa por hora debe ser un valor positivo")
    private BigDecimal valorTarifaHora;

    private String observaciones;
}
