package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.Gasto;

import java.util.List;

public interface GastoDrivingPort {

  List<Gasto> buscarTodos(String instantaneaId);

  Gasto buscarPorId(String id);

  Gasto crear(Gasto gasto);

  void eliminar(String id);
}
