package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.BancoBalance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BancoBalanceDrivingPort {

  List<BancoBalance> listarPorEntidad(String entidad, Integer limit, String order);

  @Transactional
  BancoBalance crear(BancoBalance balance);

  @Transactional
  BancoBalance actualizar(String id, BancoBalance balance);

  @Transactional
  void eliminar(String id);
}