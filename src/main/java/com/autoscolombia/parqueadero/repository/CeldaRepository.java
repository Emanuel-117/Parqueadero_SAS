package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.entity.Celda;
import com.autoscolombia.parqueadero.model.enums.EstadoCelda;
import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CeldaRepository extends JpaRepository<Celda, Long> {

    Optional<Celda> findByNumeroCeldaIgnoreCase(String numeroCelda);

    boolean existsByNumeroCeldaIgnoreCase(String numeroCelda);

    List<Celda> findByActivoTrue();

    List<Celda> findByTipoPermitidoAndEstadoAndActivoTrue(TipoVehiculo tipoPermitido, EstadoCelda estado);

    long countByEstadoAndActivoTrue(EstadoCelda estado);

    @Query("SELECT c FROM Celda c WHERE c.tipoPermitido = :tipo AND c.estado = 'DISPONIBLE' AND c.activo = TRUE ORDER BY c.numeroCelda ASC")
    List<Celda> findCeldasDisponiblesPorTipo(@Param("tipo") TipoVehiculo tipo);
}
