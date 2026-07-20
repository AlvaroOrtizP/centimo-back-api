package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.OperacionInversion;

import java.util.List;

public interface OperacionInversionDrivingPort {

  List<OperacionInversion> buscarTodas(String cuentaId);

  OperacionInversion buscarPorId(String id);

  OperacionInversion crear(OperacionInversion operacion);

  void eliminar(String id);
}
