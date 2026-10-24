package com.autoscolombia.parqueadero.dto.usuario;

import com.autoscolombia.parqueadero.model.enums.RolUsuario;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String username;
    private String nombreCompleto;
    private String email;
    private RolUsuario rol;
    private Boolean activo;
    private OffsetDateTime fechaCreacion;
}
