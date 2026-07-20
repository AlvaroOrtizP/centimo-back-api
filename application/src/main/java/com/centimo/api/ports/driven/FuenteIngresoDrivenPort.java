package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.FuenteIngreso;

import java.util.List;
import java.util.Optional;

public interface FuenteIngresoDrivenPort {

  List<FuenteIngreso> findByInstantaneaId(String instantaneaId);

  Optional<FuenteIngreso> findById(String id);

  FuenteIngreso save(FuenteIngreso ingreso);

  void deleteById(String id);
}
