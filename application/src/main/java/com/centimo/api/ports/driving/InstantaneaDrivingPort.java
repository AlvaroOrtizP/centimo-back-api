package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.InstantaneaMensual;

import java.math.BigDecimal;
import java.util.List;

public interface InstantaneaDrivingPort {

  List<InstantaneaMensual> buscarTodas(Integer anio, Integer mes, String cuentaId);

  InstantaneaMensual buscarPorId(String id);

  InstantaneaMensual crear(InstantaneaMensual instantanea);

  InstantaneaMensual upsert(String cuentaId, Integer anio, Integer mes, BigDecimal saldo, BigDecimal deltaIngresos, BigDecimal gastos);

  InstantaneaMensual actualizar(String id, InstantaneaMensual instantanea);

  void eliminar(String id);
}
