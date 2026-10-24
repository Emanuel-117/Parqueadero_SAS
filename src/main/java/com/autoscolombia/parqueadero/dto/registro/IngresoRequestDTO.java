package com.autoscolombia.parqueadero.dto.registro;

import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngresoRequestDTO {

    @NotBlank(message = "La placa del vehículo es obligatoria")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "La placa debe ser alfanumérica sin guiones ni caracteres especiales")
    private String placa;

    @NotNull(message = "El tipo de vehículo es obligatorio (AUTOMOVIL, MOTO)")
    private TipoVehiculo tipoVehiculo;

    @NotNull(message = "El ID de la celda elegida es obligatorio")
    private Long idCelda;

    private Long idUsuarioOperador;

    private String marca;

    private String modelo;

    private String color;

    private String observacionesNovedad;

    @Positive(message = "La tarifa por hora debe ser mayor que cero")
    private BigDecimal valorTarifaHora;
}
