package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.dto.novedad.NovedadCreateDTO;
import com.autoscolombia.parqueadero.dto.novedad.NovedadDTO;
import com.autoscolombia.parqueadero.service.NovedadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/novedades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NovedadController {

    private final NovedadService novedadService;

    /**
     * POST /api/novedades
     * Registra observaciones, incidentes o daños en la entrada o durante la estancia.
     * Respuesta: 201 Created / 400 Bad Request / 404 Not Found
     */
    @PostMapping
    public ResponseEntity<NovedadDTO> registrarNovedad(@Valid @RequestBody NovedadCreateDTO dto) {
        NovedadDTO response = novedadService.registrarNovedad(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/novedades/registro/{idRegistro}
     * Recupera el listado cronológico de novedades asociadas a una estancia.
     * Respuesta: 200 OK / 404 Not Found
     */
    @GetMapping("/registro/{idRegistro}")
    public ResponseEntity<List<NovedadDTO>> obtenerPorRegistro(@PathVariable Long idRegistro) {
        List<NovedadDTO> novedades = novedadService.obtenerNovedadesPorRegistro(idRegistro);
        return ResponseEntity.ok(novedades);
    }
}
