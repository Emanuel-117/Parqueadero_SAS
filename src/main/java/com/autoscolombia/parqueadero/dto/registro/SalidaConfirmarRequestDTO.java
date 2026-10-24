package com.autoscolombia.parqueadero.dto.registro;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalidaConfirmarRequestDTO {
    private String placa;
    private Long idCelda;
    private String numeroCelda;
    private Long idUsuarioSalida;
    private String observaciones;
}
