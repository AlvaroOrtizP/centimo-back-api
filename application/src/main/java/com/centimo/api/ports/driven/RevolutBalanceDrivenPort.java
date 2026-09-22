package com.centimo.api.ports.driven;

import com.centimo.api.domain.models.RevolutBalance;

import java.util.List;
import java.util.Optional;

public interface RevolutBalanceDrivenPort {

  Optional<RevolutBalance> findById(String id);

  Optional<RevolutBalance> findByMes(String mes);

  List<RevolutBalance> findAll(Integer limit, String order);

  RevolutBalance guardar(RevolutBalance balance);

  void eliminar(String id);
}
