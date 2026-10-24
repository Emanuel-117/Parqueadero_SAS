package com.autoscolombia.parqueadero.dto.novedad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovedadCreateDTO {

    @NotNull(message = "El identificador del registro de ingreso es obligatorio")
    private Long idRegistroIngreso;

    @NotNull(message = "El identificador del usuario que reporta es obligatorio")
    private Long idUsuario;

    @NotBlank(message = "El tipo de novedad es obligatorio")
    @Size(max = 50, message = "El tipo de novedad no puede exceder 50 caracteres")
    private String tipoNovedad;

    @NotBlank(message = "La descripción de la novedad es obligatoria")
    private String descripcion;

    @Size(max = 255, message = "La URL de evidencia no puede exceder 255 caracteres")
    private String evidenciaUrl;
}
