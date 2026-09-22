package com.centimo.api.database.repositories;

import com.centimo.api.database.models.B100BalanceMO;
import com.centimo.api.domain.enums.TipoSubcuentaB100;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface B100BalanceRepository extends JpaRepository<B100BalanceMO, String> {

  Optional<B100BalanceMO> findByTipoSubcuentaAndMes(TipoSubcuentaB100 tipoSubcuenta, String mes);

  List<B100BalanceMO> findByTipoSubcuentaAndMesLessThanEqual(TipoSubcuentaB100 tipoSubcuenta, String mes, Pageable pageable);

  List<B100BalanceMO> findByTipoSubcuentaAndMesGreaterThanEqual(TipoSubcuentaB100 tipoSubcuenta, String mes, Pageable pageable);
}