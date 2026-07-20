package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.AsignacionSalario;

import java.util.List;

public interface AsignacionSalarioDrivingPort {

  List<AsignacionSalario> buscarTodas(Integer anio, Integer mes);

  AsignacionSalario buscarPorId(String id);

  AsignacionSalario crear(AsignacionSalario asignacion);

  AsignacionSalario actualizar(String id, AsignacionSalario asignacion);

  void eliminar(String id);
}
