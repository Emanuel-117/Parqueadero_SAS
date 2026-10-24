package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.dto.registro.SalidaConfirmarRequestDTO;
import com.autoscolombia.parqueadero.dto.registro.SalidaConfirmarResponseDTO;
import com.autoscolombia.parqueadero.service.SalidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salidas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalidaController {

    private final SalidaService salidaService;

    /**
     * POST /api/salidas/confirmar
     * Busca la estancia activa por placa o celda, calcula el tiempo transcurrido (horas/minutos),
     * recupera el historial de novedades asociadas y libera la celda cambiándola a 'DISPONIBLE'.
     * Respuesta: 200 OK / 400 Bad Request / 404 Not Found
     */
    @PostMapping("/confirmar")
    public ResponseEntity<SalidaConfirmarResponseDTO> confirmarSalida(@RequestBody SalidaConfirmarRequestDTO dto) {
        SalidaConfirmarResponseDTO response = salidaService.confirmarSalida(dto);
        return ResponseEntity.ok(response);
    }
}
