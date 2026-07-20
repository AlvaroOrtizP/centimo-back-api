package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Gasto;

import java.util.List;
import java.util.Optional;

public interface GastoDrivenPort {

  List<Gasto> findByInstantaneaId(String instantaneaId);

  Optional<Gasto> findById(String id);

  Gasto save(Gasto gasto);

  void deleteById(String id);
}
