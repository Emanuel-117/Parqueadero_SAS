package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.entity.RegistroIngreso;
import com.autoscolombia.parqueadero.model.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroIngresoRepository extends JpaRepository<RegistroIngreso, Long> {

    /**
     * Consulta optimizada con JOIN FETCH para obtener todos los registros en un estado
     * específico (ej: ACTIVO) evitando el problema de consultas N+1.
     */
    @Query("SELECT r FROM RegistroIngreso r " +
           "JOIN FETCH r.vehiculo v " +
           "JOIN FETCH r.celda c " +
           "JOIN FETCH r.usuarioIngreso ui " +
           "LEFT JOIN FETCH r.usuarioSalida us " +
           "WHERE r.estado = :estado " +
           "ORDER BY r.fechaIngreso DESC")
    List<RegistroIngreso> findAllByEstadoWithDetails(@Param("estado") EstadoRegistro estado);

    /**
     * Búsqueda personalizada del registro de ingreso ACTIVO actual para una placa específica.
     */
    @Query("SELECT r FROM RegistroIngreso r " +
           "JOIN FETCH r.vehiculo v " +
           "JOIN FETCH r.celda c " +
           "JOIN FETCH r.usuarioIngreso ui " +
           "LEFT JOIN FETCH r.usuarioSalida us " +
           "WHERE UPPER(v.placa) = UPPER(:placa) " +
           "AND r.estado = com.autoscolombia.parqueadero.model.enums.EstadoRegistro.ACTIVO")
    Optional<RegistroIngreso> findActivoByPlaca(@Param("placa") String placa);

    /**
     * Histórico de todos los ingresos (activos o finalizados) asociados a una placa.
     */
    @Query("SELECT r FROM RegistroIngreso r " +
           "JOIN FETCH r.vehiculo v " +
           "JOIN FETCH r.celda c " +
           "JOIN FETCH r.usuarioIngreso ui " +
           "LEFT JOIN FETCH r.usuarioSalida us " +
           "WHERE UPPER(v.placa) = UPPER(:placa) " +
           "ORDER BY r.fechaIngreso DESC")
    List<RegistroIngreso> findHistoricoByPlaca(@Param("placa") String placa);

    /**
     * Verifica si una placa vehicular ya tiene una estancia activa en el parqueadero.
     */
    @Query("SELECT COUNT(r) > 0 FROM RegistroIngreso r " +
           "WHERE UPPER(r.vehiculo.placa) = UPPER(:placa) " +
           "AND r.estado = com.autoscolombia.parqueadero.model.enums.EstadoRegistro.ACTIVO")
    boolean existsActivoByPlaca(@Param("placa") String placa);

    /**
     * Obtiene el registro activo ocupando una celda específica.
     */
    @Query("SELECT r FROM RegistroIngreso r " +
           "JOIN FETCH r.vehiculo v " +
           "WHERE r.celda.id = :celdaId " +
           "AND r.estado = com.autoscolombia.parqueadero.model.enums.EstadoRegistro.ACTIVO")
    Optional<RegistroIngreso> findActivoByCeldaId(@Param("celdaId") Long celdaId);

    /**
     * Consulta registros por rango de fechas de ingreso para reportes y arqueos.
     */
    List<RegistroIngreso> findByFechaIngresoBetweenOrderByFechaIngresoDesc(
            OffsetDateTime fechaInicio, OffsetDateTime fechaFin);
}
