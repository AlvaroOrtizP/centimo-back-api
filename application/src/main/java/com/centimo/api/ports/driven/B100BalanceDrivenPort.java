package com.centimo.api.ports.driven;

import com.centimo.api.domain.enums.TipoSubcuentaB100;
import com.centimo.api.domain.models.B100Balance;

import java.util.List;
import java.util.Optional;

public interface B100BalanceDrivenPort {

  Optional<B100Balance> findById(String id);

  Optional<B100Balance> findByTipoSubcuentaAndMes(TipoSubcuentaB100 tipoSubcuenta, String mes);

  List<B100Balance> findByTipoSubcuenta(TipoSubcuentaB100 tipoSubcuenta, String mes, Integer limit, String order);

  B100Balance guardar(B100Balance balance);

  void eliminar(String id);
}