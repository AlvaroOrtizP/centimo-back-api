package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.Compromiso;

import java.util.List;

public interface CompromisoDrivingPort {

  List<Compromiso> buscarTodos();

  List<Compromiso> buscarPorMes(Integer mes);

  Compromiso buscarPorId(String id);

  Compromiso crear(Compromiso compromiso);

  Compromiso actualizar(String id, Compromiso compromiso);

  void eliminar(String id);
}
