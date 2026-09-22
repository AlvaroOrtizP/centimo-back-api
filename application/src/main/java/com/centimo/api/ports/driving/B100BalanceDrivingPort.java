package com.centimo.api.ports.driving;

import com.centimo.api.domain.enums.TipoSubcuentaB100;
import com.centimo.api.domain.models.B100Balance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface B100BalanceDrivingPort {

  Optional<B100Balance> obtenerPorSubcuentaYMes(TipoSubcuentaB100 tipoSubcuenta, String mes);

  List<B100Balance> listarPorSubcuenta(TipoSubcuentaB100 tipoSubcuenta, String mes, Integer limit, String order);

  @Transactional
  B100Balance crear(B100Balance balance);

  @Transactional
  B100Balance actualizar(String id, B100Balance balance);

  @Transactional
  void eliminar(String id);
}