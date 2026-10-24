package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.entity.Vehiculo;
import com.autoscolombia.parqueadero.model.enums.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlacaIgnoreCase(String placa);

    boolean existsByPlacaIgnoreCase(String placa);

    List<Vehiculo> findByTipoVehiculo(TipoVehiculo tipoVehiculo);

    @Query("SELECT v FROM Vehiculo v WHERE UPPER(v.placa) LIKE UPPER(CONCAT('%', :filtro, '%'))")
    List<Vehiculo> searchByPlacaLike(@Param("filtro") String filtro);
}
