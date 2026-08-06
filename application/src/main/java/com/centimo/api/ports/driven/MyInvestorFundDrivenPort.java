package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.FondoMyInvestor;

import java.util.List;
import java.util.Optional;

public interface MyInvestorFundDrivenPort {

  List<FondoMyInvestor> findAll();

  Optional<FondoMyInvestor> findById(String id);

  FondoMyInvestor save(FondoMyInvestor fondo);

  void delete(String id);
}
