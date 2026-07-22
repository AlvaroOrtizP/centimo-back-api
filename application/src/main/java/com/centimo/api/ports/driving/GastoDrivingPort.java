package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.Gasto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GastoDrivingPort {

  List<Gasto> listarPorInstantanea(String instantaneaId);

  @Transactional
  Gasto crear(Gasto gasto);

  @Transactional
  void eliminar(String id, String instantaneaId);
}
