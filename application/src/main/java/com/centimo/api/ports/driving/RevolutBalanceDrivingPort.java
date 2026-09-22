package com.centimo.api.ports.driving;

import com.centimo.api.domain.models.RevolutBalance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RevolutBalanceDrivingPort {

  List<RevolutBalance> listar(Integer limit, String order);

  @Transactional
  RevolutBalance crear(RevolutBalance balance);

  @Transactional
  RevolutBalance actualizar(String id, RevolutBalance balance);

  @Transactional
  void eliminar(String id);
}
