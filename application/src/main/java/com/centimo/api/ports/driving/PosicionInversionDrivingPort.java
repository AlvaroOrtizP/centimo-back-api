package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.PosicionInversion;

import java.util.List;

public interface PosicionInversionDrivingPort {

  List<PosicionInversion> buscarTodas(String instantaneaId);

  PosicionInversion buscarPorId(String id);

  PosicionInversion crear(PosicionInversion posicion);

  void eliminar(String id);
}
