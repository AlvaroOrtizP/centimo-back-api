package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.FondoMyInvestor;

import java.util.List;

public interface FondoMyInvestorDrivingPort {

  List<FondoMyInvestor> buscarTodos();

  FondoMyInvestor buscarPorId(String id);

  FondoMyInvestor crear(FondoMyInvestor fondo);

  FondoMyInvestor actualizar(String id, FondoMyInvestor fondo);

  void eliminar(String id);
}
