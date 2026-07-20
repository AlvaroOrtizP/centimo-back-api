package com.centimo.api.database.repositories;

import com.centimo.api.database.models.AsignacionSalarioMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionSalarioRepository extends JpaRepository<AsignacionSalarioMO, String> {
  List<AsignacionSalarioMO> findByAnioAndMes(Integer anio, Integer mes);
}
