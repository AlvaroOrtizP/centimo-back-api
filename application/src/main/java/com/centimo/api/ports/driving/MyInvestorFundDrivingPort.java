package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.FondoMyInvestor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MyInvestorFundDrivingPort {

  List<FondoMyInvestor> listar();

  FondoMyInvestor obtener(String id);

  @Transactional
  FondoMyInvestor crear(FondoMyInvestor fondo);

  @Transactional
  FondoMyInvestor actualizar(String id, FondoMyInvestor fondo);

  @Transactional
  void eliminar(String id);
}