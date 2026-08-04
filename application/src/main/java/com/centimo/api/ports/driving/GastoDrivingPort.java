package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.Gasto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GastoDrivingPort {

  List<Gasto> listarPorInstantanea(String instantaneaId);

  List<Gasto> listarPorPeriodo(int year, int month);

  @Transactional
  Gasto crear(Gasto gasto);

  @Transactional
  Gasto actualizar(String id, Gasto gasto);

  @Transactional
  void eliminar(String id, String instantaneaId);
}
