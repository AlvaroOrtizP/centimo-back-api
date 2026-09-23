package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.FondoMyInvestor;

import java.util.List;
import java.util.Optional;

public interface MyInvestorFundDrivenPort {

  List<FondoMyInvestor> findAll();

  Optional<FondoMyInvestor> findById(String id);

  FondoMyInvestor guardar(FondoMyInvestor fondo);

  void eliminar(String id);
}