package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.BalanceFondo;

import java.util.List;
import java.util.Optional;

public interface FundBalanceDrivenPort {

  List<BalanceFondo> findByAnioAndMes(Integer anio, Integer mes);

  Optional<BalanceFondo> findById(String id);

  Optional<BalanceFondo> findByFondoIdAnioAndMes(String fondoId, Integer anio, Integer mes);

  BalanceFondo guardar(BalanceFondo balance);

  void eliminar(String id);
}