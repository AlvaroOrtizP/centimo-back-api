package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Gasto;

import java.util.List;
import java.util.Optional;

public interface GastoDrivenPort {

  List<Gasto> findByInstantaneaId(String instantaneaId);

  List<Gasto> findByAnioYMes(int year, int month);

  Optional<Gasto> findById(String id);

  Gasto guardar(Gasto gasto);

  void eliminar(String id);
}
