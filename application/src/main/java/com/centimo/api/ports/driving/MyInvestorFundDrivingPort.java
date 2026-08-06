package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.FondoMyInvestor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MyInvestorFundDrivingPort {

  List<FondoMyInvestor> listAll();

  FondoMyInvestor getById(String id);

  @Transactional
  FondoMyInvestor create(FondoMyInvestor fondo);

  @Transactional
  FondoMyInvestor update(String id, FondoMyInvestor fondo);

  @Transactional
  void delete(String id);
}
