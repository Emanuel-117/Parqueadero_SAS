package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.dto.celda.CeldaDTO;
import com.autoscolombia.parqueadero.dto.registro.IngresoRequestDTO;
import com.autoscolombia.parqueadero.dto.registro.RegistroIngresoResponseDTO;
import com.autoscolombia.parqueadero.dto.usuario.UsuarioDTO;
import com.autoscolombia.parqueadero.dto.vehiculo.VehiculoDTO;
import com.autoscolombia.parqueadero.exception.BusinessRuleException;
import com.autoscolombia.parqueadero.exception.ResourceNotFoundException;
import com.autoscolombia.parqueadero.model.entity.*;
import com.autoscolombia.parqueadero.model.enums.EstadoCelda;
import com.autoscolombia.parqueadero.model.enums.EstadoRegistro;
import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import com.autoscolombia.parqueadero.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class IngresoService {

    private final VehiculoRepository vehiculoRepository;
    private final CeldaRepository celdaRepository;
    private final RegistroIngresoRepository registroIngresoRepository;
    private final NovedadRepository novedadRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public RegistroIngresoResponseDTO registrarIngreso(IngresoRequestDTO dto) {
        String placaNormalizada = dto.getPlaca().trim().toUpperCase();

        // 1. Validar que el vehículo no tenga un ingreso activo previo
        if (registroIngresoRepository.existsActivoByPlaca(placaNormalizada)) {
            throw new BusinessRuleException(
                    "El vehículo con placa '" + placaNormalizada + "' ya tiene un ingreso ACTIVO en el parqueadero.");
        }

        // 2. Validar existencia, estado y compatibilidad de la celda elegida
        Celda celda = celdaRepository.findById(dto.getIdCelda())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la celda con ID: " + dto.getIdCelda()));

        if (Boolean.FALSE.equals(celda.getActivo())) {
            throw new BusinessRuleException("La celda " + celda.getNumeroCelda() + " se encuentra inactiva.");
        }

        if (celda.getEstado() != EstadoCelda.DISPONIBLE) {
            throw new BusinessRuleException(
                    "La celda " + celda.getNumeroCelda() + " no está disponible. Estado actual: " + celda.getEstado());
        }

        if (celda.getTipoPermitido() != dto.getTipoVehiculo()) {
            throw new BusinessRuleException(
                    "Incompatibilidad de tipo: La celda " + celda.getNumeroCelda() + " es exclusiva para " +
                            celda.getTipoPermitido() + " y el vehículo ingresado es " + dto.getTipoVehiculo());
        }

        // 3. Buscar o registrar vehículo en catálogo
        Vehiculo vehiculo = vehiculoRepository.findByPlacaIgnoreCase(placaNormalizada)
                .orElseGet(() -> {
                    Vehiculo nuevo = Vehiculo.builder()
                            .placa(placaNormalizada)
                            .tipoVehiculo(dto.getTipoVehiculo())
                            .marca(dto.getMarca())
                            .modelo(dto.getModelo())
                            .color(dto.getColor())
                            .fechaRegistro(OffsetDateTime.now())
                            .build();
                    return vehiculoRepository.save(nuevo);
                });

        // 4. Obtener usuario operador responsable del ingreso
        Usuario operador = resolverOperador(dto.getIdUsuarioOperador());

        // 5. Determinar tarifa por hora
        BigDecimal tarifaHora = dto.getValorTarifaHora();
        if (tarifaHora == null || tarifaHora.compareTo(BigDecimal.ZERO) <= 0) {
            tarifaHora = dto.getTipoVehiculo() == TipoVehiculo.AUTOMOVIL
                    ? new BigDecimal("5000.00")
                    : new BigDecimal("2500.00");
        }

        // 6. Cambiar estado de la celda a 'OCUPADA'
        celda.setEstado(EstadoCelda.OCUPADA);
        celdaRepository.save(celda);

        // 7. Crear y persistir el registro de ingreso activo
        RegistroIngreso registro = RegistroIngreso.builder()
                .vehiculo(vehiculo)
                .celda(celda)
                .usuarioIngreso(operador)
                .fechaIngreso(OffsetDateTime.now())
                .estado(EstadoRegistro.ACTIVO)
                .valorTarifaHora(tarifaHora)
                .observaciones(dto.getObservacionesNovedad())
                .build();

        RegistroIngreso registroGuardado = registroIngresoRepository.save(registro);

        // 8. Si se enviaron observaciones de novedad al ingresar, registrar la novedad
        if (dto.getObservacionesNovedad() != null && !dto.getObservacionesNovedad().trim().isEmpty()) {
            Novedad novedadIngreso = Novedad.builder()
                    .registroIngreso(registroGuardado)
                    .usuario(operador)
                    .fechaRegistro(OffsetDateTime.now())
                    .tipoNovedad("INGRESO_OBSERVACION")
                    .descripcion(dto.getObservacionesNovedad().trim())
                    .build();
            novedadRepository.save(novedadIngreso);
        }

        return mapearAResponse(registroGuardado);
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

    private RegistroIngresoResponseDTO mapearAResponse(RegistroIngreso r) {
        return RegistroIngresoResponseDTO.builder()
                .id(r.getId())
                .vehiculo(VehiculoDTO.builder()
                        .id(r.getVehiculo().getId())
                        .placa(r.getVehiculo().getPlaca())
                        .tipoVehiculo(r.getVehiculo().getTipoVehiculo())
                        .marca(r.getVehiculo().getMarca())
                        .modelo(r.getVehiculo().getModelo())
                        .color(r.getVehiculo().getColor())
                        .fechaRegistro(r.getVehiculo().getFechaRegistro())
                        .build())
                .celda(CeldaDTO.builder()
                        .id(r.getCelda().getId())
                        .numeroCelda(r.getCelda().getNumeroCelda())
                        .tipoPermitido(r.getCelda().getTipoPermitido())
                        .estado(r.getCelda().getEstado())
                        .activo(r.getCelda().getActivo())
                        .descripcion(r.getCelda().getDescripcion())
                        .build())
                .operadorIngreso(UsuarioDTO.builder()
                        .id(r.getUsuarioIngreso().getId())
                        .username(r.getUsuarioIngreso().getUsername())
                        .nombreCompleto(r.getUsuarioIngreso().getNombreCompleto())
                        .email(r.getUsuarioIngreso().getEmail())
                        .rol(r.getUsuarioIngreso().getRol())
                        .activo(r.getUsuarioIngreso().getActivo())
                        .fechaCreacion(r.getUsuarioIngreso().getFechaCreacion())
                        .build())
                .fechaIngreso(r.getFechaIngreso())
                .fechaSalida(r.getFechaSalida())
                .estado(r.getEstado())
                .valorTarifaHora(r.getValorTarifaHora())
                .totalPagado(r.getTotalPagado())
                .observaciones(r.getObservaciones())
                .build();
    }
}
