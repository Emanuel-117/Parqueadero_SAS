package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.dto.novedad.NovedadCreateDTO;
import com.autoscolombia.parqueadero.dto.novedad.NovedadDTO;
import com.autoscolombia.parqueadero.dto.usuario.UsuarioDTO;
import com.autoscolombia.parqueadero.exception.ResourceNotFoundException;
import com.autoscolombia.parqueadero.model.entity.Novedad;
import com.autoscolombia.parqueadero.model.entity.RegistroIngreso;
import com.autoscolombia.parqueadero.model.entity.Usuario;
import com.autoscolombia.parqueadero.repository.NovedadRepository;
import com.autoscolombia.parqueadero.repository.RegistroIngresoRepository;
import com.autoscolombia.parqueadero.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NovedadService {

    private final NovedadRepository novedadRepository;
    private final RegistroIngresoRepository registroIngresoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public NovedadDTO registrarNovedad(NovedadCreateDTO dto) {
        RegistroIngreso registro = registroIngresoRepository.findById(dto.getIdRegistroIngreso())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el registro de estancia con ID: " + dto.getIdRegistroIngreso()));

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseGet(() -> usuarioRepository.findByUsernameIgnoreCase("operador_turno1")
                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con ID: " + dto.getIdUsuario())));

        Novedad novedad = Novedad.builder()
                .registroIngreso(registro)
                .usuario(usuario)
                .fechaRegistro(OffsetDateTime.now())
                .tipoNovedad(dto.getTipoNovedad().trim())
                .descripcion(dto.getDescripcion().trim())
                .evidenciaUrl(dto.getEvidenciaUrl())
                .build();

        Novedad novedadGuardada = novedadRepository.save(novedad);
        return mapearNovedadADTO(novedadGuardada);
    }

    @Transactional(readOnly = true)
    public List<NovedadDTO> obtenerNovedadesPorRegistro(Long idRegistroIngreso) {
        if (!registroIngresoRepository.existsById(idRegistroIngreso)) {
            throw new ResourceNotFoundException("No existe registro de ingreso con ID: " + idRegistroIngreso);
        }
        return novedadRepository.findByRegistroIngresoIdOrderByFechaRegistroDesc(idRegistroIngreso)
                .stream()
                .map(this::mapearNovedadADTO)
                .toList();
    }

    private NovedadDTO mapearNovedadADTO(Novedad n) {
        return NovedadDTO.builder()
                .id(n.getId())
                .idRegistroIngreso(n.getRegistroIngreso().getId())
                .tipoNovedad(n.getTipoNovedad())
                .descripcion(n.getDescripcion())
                .evidenciaUrl(n.getEvidenciaUrl())
                .fechaRegistro(n.getFechaRegistro())
                .reportadoPor(UsuarioDTO.builder()
                        .id(n.getUsuario().getId())
                        .username(n.getUsuario().getUsername())
                        .nombreCompleto(n.getUsuario().getNombreCompleto())
                        .email(n.getUsuario().getEmail())
                        .rol(n.getUsuario().getRol())
                        .activo(n.getUsuario().getActivo())
                        .fechaCreacion(n.getUsuario().getFechaCreacion())
                        .build())
                .build();
    }
}
