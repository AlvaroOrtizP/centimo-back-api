package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.AsignacionSalario;

import java.util.List;
import java.util.Optional;

public interface AsignacionSalarioDrivenPort {

  List<AsignacionSalario> findByAnioAndMes(Integer anio, Integer mes);

  Optional<AsignacionSalario> findById(String id);

  AsignacionSalario save(AsignacionSalario asignacion);

  void deleteById(String id);
}
