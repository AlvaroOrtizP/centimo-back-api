package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.FondoMyInvestor;

import java.util.Optional;

public interface FondoMyInvestorDrivenPort {

  java.util.List<FondoMyInvestor> findAll();

  Optional<FondoMyInvestor> findById(String id);

  FondoMyInvestor save(FondoMyInvestor fondo);

  void deleteById(String id);
}
