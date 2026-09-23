package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Gasto;

import java.util.List;
import java.util.Optional;

public interface GastoDrivenPort {

  Optional<Gasto> findById(String id);

  List<Gasto> findByPeriodo(Integer year, Integer month, String order);

  List<Gasto> findAll(String order);

  Gasto guardar(Gasto gasto);

  void eliminar(String id);
}