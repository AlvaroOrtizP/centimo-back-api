package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.Cuenta;

import java.util.List;

public interface CuentaDrivingPort {

  List<Cuenta> buscarTodas(String plataformaId);

  Cuenta buscarPorId(String id);

  Cuenta crear(Cuenta cuenta);

  Cuenta actualizar(String id, Cuenta cuenta);

  void eliminar(String id);
}
