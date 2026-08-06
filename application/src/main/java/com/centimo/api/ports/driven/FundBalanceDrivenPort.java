package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.BalanceFondo;

import java.util.List;
import java.util.Optional;

public interface FundBalanceDrivenPort {

  List<BalanceFondo> findByAnioAndMes(Integer anio, Integer mes);

  Optional<BalanceFondo> findById(String id);

  BalanceFondo save(BalanceFondo balance);

  void delete(String id);
}
