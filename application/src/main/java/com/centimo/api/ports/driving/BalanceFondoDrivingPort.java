package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.BalanceFondo;

import java.util.List;

public interface BalanceFondoDrivingPort {

  List<BalanceFondo> buscarTodos(Integer anio, Integer mes);

  BalanceFondo buscarPorId(String id);

  BalanceFondo crear(BalanceFondo balance);

  BalanceFondo actualizar(String id, BalanceFondo balance);

  void eliminar(String id);
}
