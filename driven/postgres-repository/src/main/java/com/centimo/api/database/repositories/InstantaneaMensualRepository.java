package com.centimo.api.database.repositories;

import com.centimo.api.database.models.InstantaneaMensualMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstantaneaMensualRepository extends JpaRepository<InstantaneaMensualMO, String> {
  Optional<InstantaneaMensualMO> findByCuentaIdAndAnioAndMes(String cuentaId, Integer anio, Integer mes);
}
