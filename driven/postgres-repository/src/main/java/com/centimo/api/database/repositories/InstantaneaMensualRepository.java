package com.centimo.api.database.repositories;

import com.centimo.api.database.models.InstantaneaMensualMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstantaneaMensualRepository extends JpaRepository<InstantaneaMensualMO, String> {
  List<InstantaneaMensualMO> findByAnioAndMes(Integer anio, Integer mes);
  List<InstantaneaMensualMO> findByCuentaId(String cuentaId);
  List<InstantaneaMensualMO> findByAnioAndMesAndCuentaId(Integer anio, Integer mes, String cuentaId);
  Optional<InstantaneaMensualMO> findByCuentaIdAndAnioAndMes(String cuentaId, Integer anio, Integer mes);
}
