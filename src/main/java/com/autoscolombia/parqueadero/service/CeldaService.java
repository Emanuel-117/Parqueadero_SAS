package com.autoscolombia.parqueadero.service;

import com.autoscolombia.parqueadero.dto.celda.MapaCeldaDTO;
import com.autoscolombia.parqueadero.model.entity.Celda;
import com.autoscolombia.parqueadero.model.entity.RegistroIngreso;
import com.autoscolombia.parqueadero.model.enums.EstadoCelda;
import com.autoscolombia.parqueadero.model.enums.EstadoRegistro;
import com.autoscolombia.parqueadero.repository.CeldaRepository;
import com.autoscolombia.parqueadero.repository.RegistroIngresoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CeldaService {

    private final CeldaRepository celdaRepository;
    private final RegistroIngresoRepository registroIngresoRepository;

    /**
     * Retorna el mapa visual de celdas del parqueadero:
     * - Disponibles en VERDE (#22C55E)
     * - Ocupadas en ROJO (#EF4444)
     * - Mantenimiento en AMARILLO (#F59E0B)
     */
    @Transactional(readOnly = true)
    public List<MapaCeldaDTO> obtenerMapaCeldas() {
        List<Celda> celdas = celdaRepository.findByActivoTrue();

        // Indexar registros activos por id de celda
        List<RegistroIngreso> registrosActivos = registroIngresoRepository.findAllByEstadoWithDetails(EstadoRegistro.ACTIVO);
        Map<Long, RegistroIngreso> registrosPorCelda = registrosActivos.stream()
                .collect(Collectors.toMap(
                        r -> r.getCelda().getId(),
                        Function.identity(),
                        (existente, reemplazo) -> existente
                ));

        OffsetDateTime ahora = OffsetDateTime.now();

        return celdas.stream()
                .sorted(Comparator.comparing(Celda::getNumeroCelda))
                .map(celda -> {
                    MapaCeldaDTO.MapaCeldaDTOBuilder builder = MapaCeldaDTO.builder()
                            .idCelda(celda.getId())
                            .numeroCelda(celda.getNumeroCelda())
                            .tipoPermitido(celda.getTipoPermitido())
                            .estado(celda.getEstado())
                            .activo(celda.getActivo())
                            .descripcion(celda.getDescripcion());

                    if (celda.getEstado() == EstadoCelda.DISPONIBLE) {
                        builder.colorEstado("VERDE")
                               .colorHex("#22C55E");
                    } else if (celda.getEstado() == EstadoCelda.OCUPADA) {
                        builder.colorEstado("ROJO")
                               .colorHex("#EF4444");

                        RegistroIngreso reg = registrosPorCelda.get(celda.getId());
                        if (reg != null) {
                            builder.placaVehiculoActual(reg.getVehiculo().getPlaca())
                                   .tipoVehiculoActual(reg.getVehiculo().getTipoVehiculo())
                                   .fechaIngresoVehiculo(reg.getFechaIngreso());

                            Duration duracion = Duration.between(reg.getFechaIngreso(), ahora);
                            long minutosTotales = Math.max(0, duracion.toMinutes());
                            long horas = minutosTotales / 60;
                            long minutos = minutosTotales % 60;

                            builder.minutosTranscurridos(minutosTotales)
                                   .tiempoTranscurridoFormato(horas + "h " + minutos + "m");
                        }
                    } else {
                        builder.colorEstado("AMARILLO")
                               .colorHex("#F59E0B");
                    }

                    return builder.build();
                })
                .toList();
    }
}
