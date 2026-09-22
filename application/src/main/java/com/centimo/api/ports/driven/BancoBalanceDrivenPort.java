package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.BancoBalance;

import java.util.List;
import java.util.Optional;

public interface BancoBalanceDrivenPort {

  Optional<BancoBalance> findById(String id);

  Optional<BancoBalance> findByEntidadAndMes(String entidad, String mes);

  List<BancoBalance> findByEntidad(String entidad, Integer limit, String order);

  BancoBalance guardar(BancoBalance balance);

  void eliminar(String id);
}