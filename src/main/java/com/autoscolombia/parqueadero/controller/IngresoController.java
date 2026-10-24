package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.dto.registro.IngresoRequestDTO;
import com.autoscolombia.parqueadero.dto.registro.RegistroIngresoResponseDTO;
import com.autoscolombia.parqueadero.service.IngresoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingresos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IngresoController {

    private final IngresoService ingresoService;

    /**
     * POST /api/ingresos
     * Registra el ingreso de un vehículo, valida ausencia de estancias activas previas
     * y cambia el estado de la celda elegida a 'OCUPADA'.
     * Respuesta: 201 Created / 400 Bad Request
     */
    @PostMapping
    public ResponseEntity<RegistroIngresoResponseDTO> registrarIngreso(@Valid @RequestBody IngresoRequestDTO dto) {
        RegistroIngresoResponseDTO response = ingresoService.registrarIngreso(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
