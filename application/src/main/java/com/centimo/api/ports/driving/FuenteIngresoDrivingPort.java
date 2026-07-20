package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.FuenteIngreso;

import java.util.List;

public interface FuenteIngresoDrivingPort {

  List<FuenteIngreso> buscarTodas(String instantaneaId);

  FuenteIngreso buscarPorId(String id);

  FuenteIngreso crear(FuenteIngreso ingreso);

  void eliminar(String id);
}
