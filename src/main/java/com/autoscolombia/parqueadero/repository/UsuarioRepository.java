package com.autoscolombia.parqueadero.repository;

import com.autoscolombia.parqueadero.model.entity.Usuario;
import com.autoscolombia.parqueadero.model.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsernameIgnoreCase(String username);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByRolAndActivoTrue(RolUsuario rol);
}
