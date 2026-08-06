package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.BalanceFondo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FundBalanceDrivingPort {

  List<BalanceFondo> listByYearAndMonth(Integer anio, Integer mes);

  @Transactional
  BalanceFondo create(BalanceFondo balance);

  @Transactional
  BalanceFondo update(String id, BalanceFondo balance);

  @Transactional
  void delete(String id);
}
