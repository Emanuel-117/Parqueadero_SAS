package com.autoscolombia.parqueadero.dto.novedad;

import com.autoscolombia.parqueadero.dto.usuario.UsuarioDTO;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovedadDTO {
    private Long id;
    private Long idRegistroIngreso;
    private UsuarioDTO reportadoPor;
    private OffsetDateTime fechaRegistro;
    private String tipoNovedad;
    private String descripcion;
    private String evidenciaUrl;
}
