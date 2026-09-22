package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.EquitoBalance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EquitoBalanceDrivingPort {

  List<EquitoBalance> listar(Integer limit, String order);

  @Transactional
  EquitoBalance crear(EquitoBalance balance);

  @Transactional
  EquitoBalance actualizar(String id, EquitoBalance balance);

  @Transactional
  void eliminar(String id);
}