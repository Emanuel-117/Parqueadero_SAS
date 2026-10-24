package com.autoscolombia.parqueadero.dto.vehiculo;

import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculoCreateDTO {

    @NotBlank(message = "La placa es obligatoria")
    @Size(min = 5, max = 10, message = "La placa debe tener entre 5 y 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "La placa debe contener únicamente letras y números")
    private String placa;

    @NotNull(message = "El tipo de vehículo es obligatorio (AUTOMOVIL, MOTO)")
    private TipoVehiculo tipoVehiculo;

    @Size(max = 50, message = "La marca no puede exceder 50 caracteres")
    private String marca;

    @Size(max = 50, message = "El modelo no puede exceder 50 caracteres")
    private String modelo;

    @Size(max = 30, message = "El color no puede exceder 30 caracteres")
    private String color;
}
