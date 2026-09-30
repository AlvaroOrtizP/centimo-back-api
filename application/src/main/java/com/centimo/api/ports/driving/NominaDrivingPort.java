package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.Nomina;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface NominaDrivingPort {

  Optional<Nomina> obtener(String mes);

  @Transactional
  Nomina guardar(Nomina nomina);

  @Transactional
  Nomina actualizar(String mes, Nomina nomina);

  @Transactional
  void eliminar(String mes);
}