package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Nomina;

import java.util.List;
import java.util.Optional;

public interface NominaDrivenPort {

  Optional<Nomina> findByMes(String mes);

  List<Nomina> findAll();

  Nomina guardar(Nomina nomina);

  void eliminar(String mes);
}