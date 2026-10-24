package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.dto.novedad.NovedadDTO;
import com.autoscolombia.parqueadero.dto.registro.SalidaConfirmarRequestDTO;
import com.autoscolombia.parqueadero.dto.registro.SalidaConfirmarResponseDTO;
import com.autoscolombia.parqueadero.dto.usuario.UsuarioDTO;
import com.autoscolombia.parqueadero.exception.BusinessRuleException;
import com.autoscolombia.parqueadero.exception.ResourceNotFoundException;
import com.autoscolombia.parqueadero.model.entity.*;
import com.autoscolombia.parqueadero.model.enums.EstadoCelda;
import com.autoscolombia.parqueadero.model.enums.EstadoRegistro;
import com.autoscolombia.parqueadero.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalidaService {

    private final RegistroIngresoRepository registroIngresoRepository;
    private final CeldaRepository celdaRepository;
    private final NovedadRepository novedadRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public SalidaConfirmarResponseDTO confirmarSalida(SalidaConfirmarRequestDTO dto) {
        // 1. Buscar la estancia activa por placa o celda
        RegistroIngreso registro = buscarEstanciaActiva(dto);

        OffsetDateTime fechaIngreso = registro.getFechaIngreso();
        OffsetDateTime fechaSalida = OffsetDateTime.now();

        // 2. Calcular tiempo transcurrido (horas / minutos)
        Duration duracion = Duration.between(fechaIngreso, fechaSalida);
        long minutosTotales = Math.max(0, duracion.toMinutes());
        long horasCompletas = minutosTotales / 60;
        long minutosRestantes = minutosTotales % 60;

        String formatoTiempo = horasCompletas + " hora(s) y " + minutosRestantes + " minuto(s)";

        // 3. Liquidar valor a pagar (hora o fracción)
        BigDecimal tarifaHora = registro.getValorTarifaHora() != null
                ? registro.getValorTarifaHora()
                : new BigDecimal("5000.00");

        long horasACobrar = (minutosTotales == 0) ? 1 : ((minutosTotales + 59) / 60);
        BigDecimal totalCobrado = tarifaHora.multiply(BigDecimal.valueOf(horasACobrar));

        // 4. Resolver operador que registra la salida
        Usuario operadorSalida = resolverOperador(dto.getIdUsuarioSalida());

        // 5. Recuperar historial de novedades asociadas a la estancia
        List<Novedad> novedades = novedadRepository.findByRegistroIngresoIdOrderByFechaRegistroDesc(registro.getId());
        List<NovedadDTO> novedadesDTO = novedades.stream()
                .map(this::mapearNovedadADTO)
                .toList();

        // 6. Actualizar y finalizar registro de ingreso
        registro.setFechaSalida(fechaSalida);
        registro.setEstado(EstadoRegistro.FINALIZADO);
        registro.setTotalPagado(totalCobrado);
        registro.setUsuarioSalida(operadorSalida);
        if (dto.getObservaciones() != null && !dto.getObservaciones().trim().isEmpty()) {
            String obsActual = registro.getObservaciones() != null ? registro.getObservaciones() + " | " : "";
            registro.setObservaciones(obsActual + "Salida: " + dto.getObservaciones().trim());
        }
        registroIngresoRepository.save(registro);

        // 7. Cambiar celda a estado 'DISPONIBLE'
        Celda celda = registro.getCelda();
        celda.setEstado(EstadoCelda.DISPONIBLE);
        celdaRepository.save(celda);

        return SalidaConfirmarResponseDTO.builder()
                .idRegistro(registro.getId())
                .placa(registro.getVehiculo().getPlaca())
                .tipoVehiculo(registro.getVehiculo().getTipoVehiculo())
                .idCelda(celda.getId())
                .numeroCelda(celda.getNumeroCelda())
                .fechaIngreso(fechaIngreso)
                .fechaSalida(fechaSalida)
                .horasTranscurridas(horasCompletas)
                .minutosTranscurridos(minutosRestantes)
                .tiempoTranscurridoFormato(formatoTiempo)
                .valorTarifaHora(tarifaHora)
                .totalCobrado(totalCobrado)
                .operadorSalida(operadorSalida.getNombreCompleto())
                .observaciones(registro.getObservaciones())
                .historialNovedades(novedadesDTO)
                .mensaje("Salida confirmada exitosamente. Celda " + celda.getNumeroCelda() + " liberada y disponible.")
                .build();
    }

    private RegistroIngreso buscarEstanciaActiva(SalidaConfirmarRequestDTO dto) {
        if (dto.getPlaca() != null && !dto.getPlaca().trim().isEmpty()) {
            String placaNormalizada = dto.getPlaca().trim().toUpperCase();
            return registroIngresoRepository.findActivoByPlaca(placaNormalizada)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró ninguna estancia activa para el vehículo con placa: " + placaNormalizada));
        }

        if (dto.getIdCelda() != null) {
            return registroIngresoRepository.findActivoByCeldaId(dto.getIdCelda())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró estancia activa en la celda con ID: " + dto.getIdCelda()));
        }

        if (dto.getNumeroCelda() != null && !dto.getNumeroCelda().trim().isEmpty()) {
            Celda celda = celdaRepository.findByNumeroCeldaIgnoreCase(dto.getNumeroCelda().trim())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No existe la celda: " + dto.getNumeroCelda()));
            return registroIngresoRepository.findActivoByCeldaId(celda.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No hay ningún vehículo activo estacionado en la celda " + celda.getNumeroCelda()));
        }

        throw new BusinessRuleException("Debe proporcionar la placa del vehículo o la celda (ID o número) para confirmar la salida.");
    }

    private Usuario resolverOperador(Long idUsuario) {
        if (idUsuario != null) {
            return usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró el operador con ID: " + idUsuario));
        }
        return usuarioRepository.findByUsernameIgnoreCase("operador_turno1")
                .orElseGet(() -> usuarioRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No hay usuarios operadores configurados")));
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
