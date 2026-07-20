package com.centimo.api.usecases;

import com.centimo.api.domain.exceptions.NotFoundException;
import com.centimo.api.domain.models.BalanceFondo;
import com.centimo.api.ports.driven.BalanceFondoDrivenPort;
import com.centimo.api.ports.driving.BalanceFondoDrivingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceFondoUseCase implements BalanceFondoDrivingPort {

  private final BalanceFondoDrivenPort balanceDrivenPort;

  @Override
  @Transactional(readOnly = true)
  public List<BalanceFondo> buscarTodos(Integer anio, Integer mes) {
    if (anio != null && mes != null) {
      return balanceDrivenPort.findByAnioAndMes(anio, mes);
    }
    return balanceDrivenPort.findByAnioAndMes(null, null);
  }

  @Override
  @Transactional(readOnly = true)
  public BalanceFondo buscarPorId(String id) {
    return balanceDrivenPort.findById(id)
      .orElseThrow(() -> new NotFoundException("BalanceFondo", id));
  }

  @Override
  @Transactional
  public BalanceFondo crear(BalanceFondo balance) {
    return balanceDrivenPort.save(balance);
  }

  @Override
  @Transactional
  public BalanceFondo actualizar(String id, BalanceFondo balance) {
    buscarPorId(id);
    balance.setId(id);
    return balanceDrivenPort.save(balance);
  }

  @Override
  @Transactional
  public void eliminar(String id) {
    buscarPorId(id);
    balanceDrivenPort.deleteById(id);
  }
}
