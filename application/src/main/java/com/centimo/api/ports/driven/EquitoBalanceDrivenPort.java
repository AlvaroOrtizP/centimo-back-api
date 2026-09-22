package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.EquitoBalance;

import java.util.List;
import java.util.Optional;

public interface EquitoBalanceDrivenPort {

  Optional<EquitoBalance> findById(String id);

  Optional<EquitoBalance> findByMes(String mes);

  List<EquitoBalance> findAll(Integer limit, String order);

  EquitoBalance guardar(EquitoBalance balance);

  void eliminar(String id);
}