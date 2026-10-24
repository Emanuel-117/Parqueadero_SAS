package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.dto.celda.MapaCeldaDTO;
import com.autoscolombia.parqueadero.service.CeldaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/celdas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CeldaController {

    private final CeldaService celdaService;

    /**
     * GET /api/celdas
     * Retorna el estado actual del mapa de celdas con semaforización:
     * - Disponibles en Verde (#22C55E)
     * - Ocupadas en Rojo (#EF4444)
     * Respuesta: 200 OK
     */
    @GetMapping
    public ResponseEntity<List<MapaCeldaDTO>> obtenerMapaCeldas() {
        List<MapaCeldaDTO> celdas = celdaService.obtenerMapaCeldas();
        return ResponseEntity.ok(celdas);
    }
}
