package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.UrbanitaeBalance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UrbanitaeBalanceDrivingPort {

  List<UrbanitaeBalance> listar(Integer limit, String order);

  @Transactional
  UrbanitaeBalance crear(UrbanitaeBalance balance);

  @Transactional
  UrbanitaeBalance actualizar(String id, UrbanitaeBalance balance);

  @Transactional
  void eliminar(String id);
}