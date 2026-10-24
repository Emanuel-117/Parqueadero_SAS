package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.entity.Novedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NovedadRepository extends JpaRepository<Novedad, Long> {

    @Query("SELECT n FROM Novedad n JOIN FETCH n.usuario u WHERE n.registroIngreso.id = :registroIngresoId ORDER BY n.fechaRegistro DESC")
    List<Novedad> findByRegistroIngresoIdOrderByFechaRegistroDesc(@Param("registroIngresoId") Long registroIngresoId);

    List<Novedad> findByUsuarioId(Long idUsuario);
}
