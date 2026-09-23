package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.BalanceFondo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FundBalanceDrivingPort {

  List<BalanceFondo> listarPorMes(Integer anio, Integer mes);

  @Transactional
  BalanceFondo crear(BalanceFondo balance);

  @Transactional
  BalanceFondo actualizar(String id, BalanceFondo balance);

  @Transactional
  void eliminar(String id);
}