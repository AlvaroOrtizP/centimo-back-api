package com.centimo.api.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.centimo.api.database.models.UsuarioMO;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioMO, String> {

	Optional<UsuarioMO> findByUsername(String username);
}
