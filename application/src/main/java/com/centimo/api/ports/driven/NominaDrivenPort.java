package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.Nomina;

import java.util.Optional;

public interface NominaDrivenPort {
  Optional<Nomina> findByAnioAndMes(Integer anio, Integer mes);

  Nomina guardar(Nomina nomina);
}
