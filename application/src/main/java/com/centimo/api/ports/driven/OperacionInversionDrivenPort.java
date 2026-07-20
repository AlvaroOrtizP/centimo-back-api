package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.OperacionInversion;

import java.util.List;
import java.util.Optional;

public interface OperacionInversionDrivenPort {

  List<OperacionInversion> findByCuentaId(String cuentaId);

  Optional<OperacionInversion> findById(String id);

  OperacionInversion save(OperacionInversion operacion);

  void deleteById(String id);
}
