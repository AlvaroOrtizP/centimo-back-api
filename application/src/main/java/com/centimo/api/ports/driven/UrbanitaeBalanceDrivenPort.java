package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.UrbanitaeBalance;

import java.util.List;
import java.util.Optional;

public interface UrbanitaeBalanceDrivenPort {

  Optional<UrbanitaeBalance> findById(String id);

  Optional<UrbanitaeBalance> findByMes(String mes);

  List<UrbanitaeBalance> findByMesIn(List<String> meses);

  List<UrbanitaeBalance> findAll(Integer limit, String order);

  UrbanitaeBalance guardar(UrbanitaeBalance balance);

  void eliminar(String id);
}