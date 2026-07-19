package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.Plataforma;

import java.util.List;

public interface PlataformaDrivingPort {

  List<Plataforma> buscarTodas();

  Plataforma buscarPorId(String id);

  Plataforma crear(Plataforma plataforma);

  Plataforma actualizar(String id, Plataforma plataforma);

  void eliminar(String id);
}
