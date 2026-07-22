package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.Nomina;

import java.util.Optional;

public interface NominaDrivingPort {
  Optional<Nomina> obtenerPorFecha(Integer year, Integer month);

  Nomina crear(Nomina nuevaNomina);
}
